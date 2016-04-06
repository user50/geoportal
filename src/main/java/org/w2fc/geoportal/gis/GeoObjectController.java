package org.w2fc.geoportal.gis;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.hibernate.Hibernate;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w2fc.conf.Constants;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.domain.AddnsRating;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectProperties;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.gis.model.DeleteObjectsModel;
import org.w2fc.geoportal.gis.model.FeedbackModel;
import org.w2fc.geoportal.gis.model.GeoObjectJSONModel;
import org.w2fc.geoportal.gis.model.PortalObjectModel;
import org.w2fc.geoportal.gis.model.PortalObjectTagModel;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.SendEmail;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


@Controller
@RequestMapping(value = "/geo")
public class GeoObjectController extends AbstractController<GeoObject, GeoObjectDao, Long> {

    final Logger logger = LoggerFactory.getLogger(GeoObjectController.class);
    
    @Autowired
    private ServiceRegistry serviceRegistry;
    
    @Override
    public void setAutowiredDao(GeoObjectDao dao) {
        setDao(dao);
    }

	@PostConstruct
	public void init() {
		setAutowiredDao(serviceRegistry.getGeoObjectDao());
	}

    @RequestMapping(value = "/list_by_layer_id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public @ResponseBody List<GeoObjectUI> getGisObjectsByIds(@RequestParam Long layerId) {
        GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);

        AddnsRating r = serviceRegistry.getRatingDao().get(layerId);
        if (null == r) {
            r = new AddnsRating();
            r.setClicks(1L);
            r.setGeoLayer(layer);
        } else {
            r.setClicks(r.getClicks() + 1);
        }
        serviceRegistry.getRatingDao().update(r, true);

        if(layer.getMetadata().getViewByObject()){
        	List<GeoObject> listByLayerId = serviceRegistry.getGeoObjectDao().listByLayerIdPropertiesFetched(layerId);
        	/*if(!layer.getMetadata().getCoordinateSystem().equalsIgnoreCase("WGS84")){
        		listByLayerId = CoordinateTransformer.transformFrom(layer.getMetadata().getCoordinateSystem(), listByLayerId);
        	}*/
        	return ObjectFactory.createGeoObjectUIAdapterList(listByLayerId);
        }else{
        	List<GeoObject> listByLayerId = serviceRegistry.getGeoObjectDao().listByLayerIdNotManaged(layerId);
        	/*if(!layer.getMetadata().getCoordinateSystem().equalsIgnoreCase("WGS84")){
        		listByLayerId = CoordinateTransformer.transformFrom(layer.getMetadata().getCoordinateSystem(), listByLayerId);
        	}*/
        	if(listByLayerId.size() < 5000){
        		return ObjectFactory.createGeoObjectUIAdapterListLite(listByLayerId);
        	}else{
        		return ObjectFactory.createGeoObjectUIAdapterListLiteWithoutGeom(listByLayerId);
        	}
        }
    }
    
    
    @RequestMapping(value="/getdialog/{id}")
    @Transactional(readOnly=true)
    public String getObjectDialogById(@PathVariable Long id, Model model){
        GeoObject object = serviceRegistry.getGeoObjectDao().getPropertiesFetched(id);
        GeoUser creator = object.getCreatedBy();
        if(creator != null){
            model.addAttribute("creatorName",  creator.getFullName());
        }else{
            model.addAttribute("creatorName",  "нет информации");
        }
        GeoUser editor = object.getChangedBy();
        if(editor != null){
            model.addAttribute("editorName",  editor.getFullName());
        }else{
            model.addAttribute("editorName",  "нет информации");
        }   
		Boolean isEditor = false;
		if (Constants.isEditor()) {
			isEditor = true;
		} else {
			List<GeoUser> users = serviceRegistry.getGeoObjectDao()
					.getAllowedUsersByObjectId(id);
			GeoUser currentUser = serviceRegistry.getUserDao()
					.getCurrentGeoUser();
			for (GeoUser user : users) {
				if (user.getId() == currentUser.getId()) {
					isEditor = true;
					break;
				}
			}
		}
		model.addAttribute("editor", isEditor);
        model.addAttribute("portalObject", new GeoObjectUIAdapter(object));
        if(Constants.isAdministrator()){
        	model.addAttribute("layers", serviceRegistry.getLayerDao().list());
        }else{
        	model.addAttribute("layers", serviceRegistry.getLayerDao().listTreeLayersEditable(serviceRegistry.getUserDao().getCurrentGeoUser().getId()));
        }
        Boolean isPermissionObject = false;
		if(serviceRegistry.getACLDao().getByObjectId(id) != null) isPermissionObject = true;
		model.addAttribute("permissionObject", isPermissionObject);
		model.addAttribute("areaCalc",serviceRegistry.getGeoObjectDao().getArea(id));
        return "markup/object/ObjectEdit";
    }
    
    @RequestMapping(value="/get/{id}")
    @Transactional(readOnly=true)
    @ResponseBody
    public GeoObjectUIAdapter getObjectById(@PathVariable Long id){
        GeoObject object = serviceRegistry.getGeoObjectDao().getPropertiesFetched(id);
        return new GeoObjectUIAdapter(object);
    }
    
    @Cacheable("objectIconPermanent")
    @RequestMapping(value = "/flagicon/", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getFlagImageDefault() {
    	return getFlagImage(-1L);
    }
    
    @Cacheable("objectIconPermanent")
    @RequestMapping(value = "/flagicon/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @Transactional
    @ResponseBody
    public byte[] getFlagImage(@PathVariable Long id) {
        GeoLayer layer = serviceRegistry.getLayerDao().get(id);
        try {
        	Blob imgString = layer.getIcon();
        	if (imgString == null)return errorIcon();
            BufferedImage overlay = ImageIO.read(imgString.getBinaryStream());
            int w = 25;
            int h = 41;
            BufferedImage combined = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combined.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            // paint both images, preserving the alpha channels
            g.drawImage(overlay, 0, 0, w, h,  null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(combined, "PNG", new MemoryCacheImageOutputStream(
                    baos));
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;

        } catch (Exception e) {
            return errorIcon();
        }
    }
    
    @Cacheable("objectIconPermanent")
    @RequestMapping(value = "/object/flagicon/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @Transactional
	@ResponseBody
	public byte[] getObjectFlagImage(@PathVariable Long id) {
	    GeoObjectProperties objProperties = serviceRegistry.getGeoObjectPropertiesDao().get(id);
	    try {
	    	if(null == objProperties || objProperties.getIcon() == null){
	    		throw new Exception("Object icon is not defined");
	    	}
	    	Blob imgString = objProperties.getIcon();
	        BufferedImage overlay = ImageIO.read(imgString.getBinaryStream());
	        int w = 25;
	        int h = 41;
	        BufferedImage combined = new BufferedImage(w, h,
	                BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = combined.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        // paint both images, preserving the alpha channels
	        g.drawImage(overlay, 0, 0, w, h,  null);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(combined, "PNG", new MemoryCacheImageOutputStream(
	                baos));
	        baos.flush();
	        byte[] imageInByte = baos.toByteArray();
	        baos.close();
	        return imageInByte;

	    } catch (Exception e) {
	        logger.error(e.getLocalizedMessage());
	        GeoObject object = (objProperties!=null)?objProperties.getGeoObject():serviceRegistry.getGeoObjectDao().get(id);
	        Set<GeoLayer> layers = object.getGeoLayers();
	        if(layers.size() != 1)return errorIcon();
	        Long layerId = layers.iterator().next().getId();
			return getFlagImage(layerId);
	    }
	}
    
    
    private byte[] errorIcon() {
        try {
            BufferedImage image = ImageIO.read(new File(serviceRegistry.getServletContext().getRealPath("/css/images/error-icon.png")));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", new MemoryCacheImageOutputStream(baos));
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
            return new byte[0];
        }
    }
    
    @CacheEvict(value = "objectIconPermanent", allEntries=true)
    @RequestMapping(value = "/object/uploadIcon", method = RequestMethod.POST)
    @PreAuthorize("@geoportalSecurity.isObjectAllowed(#objModel)")
    @Transactional
    public @ResponseBody Long uploadIcon( 
            @ModelAttribute PortalObjectModel objModel,
            @RequestParam MultipartFile file) throws IOException{

        Blob blob = Hibernate.createBlob(file.getInputStream());
        GeoObjectProperties objProperties = serviceRegistry.getGeoObjectPropertiesDao().get(objModel.getId());
        if(objProperties == null){
        	objProperties = ObjectFactory.createGeoObjectProperties(serviceRegistry.getGeoObjectDao().get(objModel.getId()));
        	objProperties.setIcon(blob);
        	serviceRegistry.getGeoObjectPropertiesDao().add(objProperties);
        }else{
        	objProperties.setIcon(blob);
        	serviceRegistry.getGeoObjectPropertiesDao().update(objProperties, true);
        }
        return objProperties.getId();
    }

    
    @RequestMapping(value = "/gis_objects_create", method = RequestMethod.POST)
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
	@Transactional
	public @ResponseBody
	GeoObjectUIAdapter createGisObject(@RequestParam String obj, @RequestParam String type, @RequestParam Long layerId, @RequestParam String name) {
    	GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
		FeatureJSON fjson = new FeatureJSON();
		// be sure to strip whitespace
		Reader reader = new StringReader(obj);
		try {
				SimpleFeature object = fjson.readFeature( reader );
				GeoObject gisObject = ObjectFactory.createGeoObject(name, object);

				Set<GeoLayer> geoLayers = new HashSet<GeoLayer>();
				geoLayers.add(layer);
				checkArea(gisObject);
				gisObject.setGeoLayers(geoLayers);
				layer.getGeoObjects().add(gisObject);
				gisObject.setCreatedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
				gisObject.setCreated(Calendar.getInstance().getTime());
				serviceRegistry.getGeoObjectDao().add(gisObject);
				return new GeoObjectUIAdapter(gisObject);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;

	}
    
   

	@RequestMapping(value = "/getshapebyid", method = RequestMethod.POST)
	public @ResponseBody
	String getShapeObject(@RequestParam Long id) {
		//Way shape = cartographyService.getWayById(id);
    	GeoObject shapeObj = serviceRegistry.getGeoObjectDao().get(id);
		Geometry g1 = shapeObj.getTheGeom();
		GeometryJSON g = new GeometryJSON(10);
		StringWriter writer = new StringWriter();
		try {
			g.write(g1, writer);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return writer.toString();
	}
    
    @RequestMapping(value = "/getpropertiesbyid", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody
    List<Map<String, String>> getObjectProperties(@RequestParam Long id) {
		GeoObject object = serviceRegistry.getGeoObjectDao().get(id);
		List<Map<String, String>> tags = new GeoObjectUIAdapter(object).getTags();
		Map<String,String> area = new HashMap<String, String>();
		area.put("id", "");
		area.put("key", "oarea");
		area.put("value", serviceRegistry.getGeoObjectDao().getArea(id).toString());
		tags.add(area );
		return tags;
	}
    
    @RequestMapping(value="/update")
    @PreAuthorize("@geoportalSecurity.isObjectAllowed(#objModel)")
	@Transactional
    public @ResponseBody GeoObjectUI updateObjectProperties(@ModelAttribute PortalObjectModel objModel) throws ParseException{
    	GeoObject object = serviceRegistry.getGeoObjectDao().get(objModel.getId());
		object.setName(objModel.getName());
		object.setFiasCode(objModel.getFiasCode());
		//object.clearTags();
		Set<GeoObjectTag> currentTags = object.getTags();
		
		if (objModel.getProperties() != null) {
			Map<Long, PortalObjectTagModel> tagsForUpdate = new HashMap<Long, PortalObjectTagModel>();
			List<PortalObjectTagModel> tagsForCreate = new ArrayList<PortalObjectTagModel>();
			for (PortalObjectTagModel t : objModel.getProperties()) {
				if (t.getId() != null) {
					tagsForUpdate.put(t.getId(), t);
				} else {
					if (t.getKey() != null)
						tagsForCreate.add(t);
				}
			}
			// проверка существующих
			Iterator<GeoObjectTag> iter = currentTags.iterator();
			while (iter.hasNext()) {
				GeoObjectTag currentTag = iter.next();
				PortalObjectTagModel t = tagsForUpdate.get(currentTag.getId());
				if (t == null) {
					iter.remove();
				} else {
					currentTag.setValue(t.getValue());
					currentTag.setKey(t.getKey());
				}
			}
			// добавим новые
			for (PortalObjectTagModel t : tagsForCreate) {
				GeoObjectTag tag = new GeoObjectTag();
				tag.setKey(t.getKey());
				tag.setValue(t.getValue());
				tag.setGeoObject(object);
				currentTags.add(tag);
			}
		} else {
			object.getTags().clear();
		}
		if("Point".equals(object.getTheGeom().getGeometryType())){
			GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
		    Coordinate coordinate = new Coordinate(objModel.getLon(), objModel.getLat());
			Point p = factory.createPoint(coordinate );
			object.setTheGeom(p);
		}else{
			Geometry g1 = new WKTReader().read(objModel.getWkt());
			object.setTheGeom(g1);
			GeoObjectProperties props = object.getGeoObjectProperties();
			if(props == null){
				props = ObjectFactory.createGeoObjectProperties(object);
			}
			props.setFillColor(objModel.getFillColor());
			props.setLineColor(objModel.getLineColor());
			props.setLineWeight(objModel.getLineWeight());
			//Check for usage in permission
			GeoACL acl = serviceRegistry.getACLDao().getByObjectId(object.getId());
			if(acl != null){
				acl.setName(objModel.getName());
				serviceRegistry.getACLDao().update(acl);
			}
		}
		
		checkArea(object);
		object.setChanged(Calendar.getInstance().getTime());
		object.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
		serviceRegistry.getGeoObjectDao().update(object, true);
		
		return new GeoObjectUIAdapter(object);
	}
    
    @RequestMapping(value = "/gis_objects_update", method = RequestMethod.POST)
    @PreAuthorize("@geoportalSecurity.isObjectsAllowed(#objects)")
    @Transactional
	public @ResponseBody
	Boolean updateGisObjects(@RequestBody List<GeoObjectJSONModel> objects) {
		FeatureJSON fjson = new FeatureJSON();
		for(GeoObjectJSONModel object : objects){
			// be sure to strip whitespace
			Reader reader = new StringReader(object.geojson);
			try {
					SimpleFeature forUpdate = fjson.readFeature( reader );
					GeoObject gis = serviceRegistry.getGeoObjectDao().get(object.id);
					Geometry g1 = (Geometry) forUpdate.getDefaultGeometryProperty().getValue();
					gis.setTheGeom(g1);
					checkArea(gis);
					gis.setChanged(Calendar.getInstance().getTime());
					gis.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
					serviceRegistry.getGeoObjectDao().update(gis);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
				return false; 
			}
		}
		return true;
	}
    
    @RequestMapping(value = "/gis_objects_delete", method = RequestMethod.POST)
    @PreAuthorize("@geoportalSecurity.isObjectsAllowed(#model) and @geoportalSecurity.isLayerEditor(#model.layerId)")
	@Transactional
    public @ResponseBody
	Boolean deleteGisObject(@RequestBody DeleteObjectsModel model) {
		List<GeoObject> aclObjs = serviceRegistry.getACLDao().listAllUsedObjects();
    	for(Long id : model.getObjIds()){
			GeoObject gis = serviceRegistry.getGeoObjectDao().get(id);
			checkArea(gis);
			gis.setChanged(Calendar.getInstance().getTime());
			gis.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
			if(gis.getGeoLayers().size() > 1){
				Iterator<GeoLayer> iter = gis.getGeoLayers().iterator();
				while(iter.hasNext()){
					GeoLayer layer = iter.next();
					if(layer.getId() == model.getLayerId()){
						Set<GeoObject> objs = layer.getGeoObjects();
						objs.remove(gis);
						iter.remove();
					}
				}
				serviceRegistry.getGeoObjectDao().update(gis);
			}else{
				if(aclObjs.contains(gis)){
					GeoACL acl = serviceRegistry.getACLDao().getByObjectId(gis.getId());
					serviceRegistry.getACLDao().remove(acl.getId());
				}
				/*gis.setName("DELETED");
				serviceRegistry.getGeoObjectDao().update(gis, true);*/
				serviceRegistry.getGeoObjectDao().remove(id);
			}
		}
		return true;
	}
    
    
    @RequestMapping(value = "/gis_objects_delete_by_layer", method = RequestMethod.POST)
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
   	@Transactional
       public @ResponseBody
   	Boolean deleteGisObjectByLayer(@RequestParam Long layerId) {
    	List<GeoObject> aclObjs = serviceRegistry.getACLDao().listAllUsedObjects();
   		GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
   		Set<GeoObject> objs = layer.getGeoObjects();
   		Iterator<GeoObject> objsIter = objs.iterator();
   		while(objsIter.hasNext()){
   			GeoObject gis = objsIter.next();
   			checkArea(gis);
   			gis.setChanged(Calendar.getInstance().getTime());
   			gis.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
   			if(gis.getGeoLayers().size() > 1){
   				Iterator<GeoLayer> iter = gis.getGeoLayers().iterator();
   				while(iter.hasNext()){
   					GeoLayer layerOwner = iter.next();
   					if(layerOwner.getId() == layerId){
   						iter.remove();
   						break;
   					}
   				}
   				objsIter.remove();
   				serviceRegistry.getGeoObjectDao().update(gis);
   			}else{
   				if(aclObjs.contains(gis)){
					GeoACL acl = serviceRegistry.getACLDao().getByObjectId(gis.getId());
					serviceRegistry.getACLDao().remove(acl.getId());
				}
   				/*gis.setName("DELETED");
   				serviceRegistry.getGeoObjectDao().update(gis, true);*/
   				serviceRegistry.getGeoObjectDao().remove(gis.getId());
   			}
   		}
   		objs.clear();
   		serviceRegistry.getLayerDao().update(layer);
   		return true;
   	}
    
    @RequestMapping(value="/feedback/{id}")
    @Transactional
    public String getFeedbackDialogById(@PathVariable Long id, Model model){
    	GeoObject gis = serviceRegistry.getGeoObjectDao().get(id);
        model.addAttribute("object", gis);
		model.addAttribute("layers", ObjectFactory.createGeoLayerUIAdapterList(gis.getGeoLayers()));
		GeoUser auth = serviceRegistry.getUserDao().getCurrentGeoUser();
		model.addAttribute("auth", auth);
        return "markup/object/Feedback";
    }
	
    @RequestMapping(value="/feedback_send")
    public @ResponseBody String sendFeedback(@ModelAttribute FeedbackModel model) throws AddressException{
		GeoObject object = serviceRegistry.getGeoObjectDao().get(model.getObjectId());
		String name = object.getName();
		String text = "Замечание об объекте: " + name + "\n" +  model.getComment(); 
    	for(String mail : model.getMails()){
			InternetAddress replyAddr = new InternetAddress(model.getFrommail());
			if(!"Success".equals(SendEmail.send(mail, new Address[]{replyAddr} , text)))return "Error";
		}
		return "Success";
	}
    
    @RequestMapping(value="/add_to_layer")
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    @Transactional
    public @ResponseBody boolean addObjectToLayerLink(@RequestParam Long objId, @RequestParam Long layerId){
    	GeoObject gis = serviceRegistry.getGeoObjectDao().get(objId);
    	checkArea(gis);
    	
    	Iterator<GeoLayer> iter = gis.getGeoLayers().iterator();
		while(iter.hasNext()){
			if(iter.next().getId() == layerId)return true;
		}
		try{
			serviceRegistry.getGeoObjectDao().addToLayer(objId, layerId);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return true;
	}
    
    private void checkArea(GeoObject gisObject) {
    	Geometry permArea = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object user = auth.getPrincipal();
    	if(user instanceof CustomUserDetails){
    		if(((CustomUserDetails)user).getPermissionArea() == null)return;
    		permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
    		if(permArea == null)return;
    		if(!permArea.contains(gisObject.getTheGeom()))throw new RuntimeException("Область редактирования недоступна");
    	}
	}
}
