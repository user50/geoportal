package org.w2fc.geoportal.layer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w2fc.conf.Constants;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.GeoLayerDao;
import org.w2fc.geoportal.domain.AddnsRating;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;
import org.w2fc.geoportal.domain.GeoLayerToRoleReference;
import org.w2fc.geoportal.domain.GeoLayerToUserReference;
import org.w2fc.geoportal.domain.GeoLayerType;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;


@Controller
@RequestMapping(value = "/layer")
public class LayerController extends AbstractController<GeoLayer, GeoLayerDao, Long>{
    
	final Logger logger = LoggerFactory.getLogger(LayerController.class);
	
    @Autowired
    ServiceRegistry serviceRegistry;
    
    @Autowired
    @Override
    public void setAutowiredDao(GeoLayerDao dao) {
        setDao(dao);
    }
    
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    List<GeoLayerUIAdapter> list() {
    	List<GeoLayerUIAdapter> result;
        if(Constants.isEditor()){
        	result = ObjectFactory
                    .createGeoLayerUIAdapterList(
                            getDao().list());
        }else{
        
        result = ObjectFactory
                .createGeoLayerUIAdapterList(
                        getDao().listTreeLayers(
                                serviceRegistry.getUserDao().getCurrentGeoUser().getId()));
        }
        
        Map<Long, Integer> ho = getDao().getHaveObjects();
		for (GeoLayerUIAdapter layer : result) {
			if (layer.getTypeId() == GeoLayerType.TYPE_WFS
					|| layer.getTypeId() == GeoLayerType.TYPE_WMSWFS) {

				if (ho.containsKey(layer.getId()) && ho.get(layer.getId()) > 0) {
					layer.setHaveObjects(1);
				} else {
					layer.setHaveObjects(0);
				}
			}
		}
        
        return result;
    }
    
    
    /* ********************************************
     *                            LAYER UI 
     * ********************************************/

    @RequestMapping(value="/get/{id}")
    @Transactional(readOnly=true)
    public String getLayerById(@PathVariable Long id, @RequestParam(required = false) Long parentId, Model model){

        double rating = 0d;
        double likes = 0d;
        Long spatialDataWeight = 0L;
        
        GeoLayer layer = getDao().get(id);

        if(null == layer){
            layer = new GeoLayer();
            layer.setParentId(parentId);
            layer.setTypeId(GeoLayerType.TYPE_WFS);
        }else{
            
            AddnsRating r = serviceRegistry.getRatingDao().get(layer.getId());
            if(null != r){
                rating = r.getClicks();
                likes = r.getLikes();
            }

            if(layer.getTypeId() == GeoLayerType.TYPE_WFS || layer.getTypeId() == GeoLayerType.TYPE_WMSWFS){
                spatialDataWeight = getDao().getObjectsCount(layer.getId());
            }
        }
        
		Boolean isEditor = false;
		Boolean templateEditor= false;
		if (Constants.isEditor()) {
			isEditor = true;
			templateEditor = true;
		} else {
			
			List<GeoUser> users = serviceRegistry.getLayerDao()
					.getAllowedUsersByLayerId((id == -1)?parentId:id);
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
		model.addAttribute("tmplEditor", templateEditor);
        model.addAttribute("rating", rating);
        model.addAttribute("likes", likes);
        model.addAttribute("portalLayer", new GeoLayerUIAdapter(layer));
        model.addAttribute("spatialDataWeight", spatialDataWeight);
        model.addAttribute("templates", serviceRegistry.getPopupTemplateDao().list());

        return "markup/layer/LayerEdit";
    }
    
    
    @RequestMapping(value="/createOrUpdate")
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerUI.id, #layerUI.parentId)")
    @CacheEvict(value = "layerIconPermanent", allEntries=true)
    public @ResponseBody Long createOrUpdateLayer(@ModelAttribute GeoLayerUIAdapter layerUI){
        
        GeoLayer layer = null;
        GeoLayerMetadata metadata = null;
        
        GeoUser currentGeoUser = serviceRegistry.getUserDao().getCurrentGeoUser();
        
        if(null == layerUI.getId()){
            layer = createGeoLayer(layerUI, currentGeoUser);
            metadata = layer.getMetadata();
            
        }else{
            layer = getDao().get(layerUI.getId());
            metadata = layer.getMetadata();
            layer.copyValuesFrom(layerUI.getGeoLayer(), "icon", "treeIcon");
        }
        if(layerUI.getTmplId() != null){
        	layer.setPopupTemplate(serviceRegistry.getPopupTemplateDao().get(layerUI.getTmplId()));
        }
        metadata.copyValuesFrom(layerUI.getGeoLayerMetadata());
        metadata.setChangedBy(currentGeoUser);
        metadata.setChanged(new Date());
        
        getDao().update(layer, true);
        
        return layer.getId();
    }
    
    
    @RequestMapping(value="/remove/{id}")
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#id)")
    public @ResponseBody Boolean deleteLayerById(@PathVariable Long id){
        
        getDao().remove(id, true);
        serviceRegistry.getRatingDao().remove(id, true);
        
        return true;
    }
    
    @CacheEvict(value = "objectIconPermanent", allEntries=true)
    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/uploadIcon", method = RequestMethod.POST)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerUI.id, #layerUI.parentId)")
    public @ResponseBody Long uploadIcon( 
            @ModelAttribute GeoLayerUIAdapter layerUI,
            @RequestParam MultipartFile file) throws IOException{

        GeoLayer l = null;

        if(null == layerUI.getId()){
            l = createGeoLayer(layerUI, serviceRegistry.getUserDao().getCurrentGeoUser());
        }else{
            l = getDao().get(layerUI.getId());
        }

        Blob blob = Hibernate.createBlob(file.getInputStream());
        l.setIcon(blob);
        getDao().update(l, true);
        serviceRegistry.getWms().resetLayer(l.getId());
        return l.getId();
    }

    private GeoLayer createGeoLayer(GeoLayerUIAdapter layerUI, GeoUser geoUser){

        GeoLayer layer = ObjectFactory.createGeoLayer();
        GeoLayerMetadata metadata = layer.getMetadata();

        metadata.setCreatedBy(geoUser);
        metadata.setCreated(new Date());

        layer.copyValuesFrom(layerUI.getGeoLayer(), "icon");
        
        GeoLayerToUserReference ref = new GeoLayerToUserReference(layer, geoUser);
        layer.getLayerToUserReferences().add(ref);
        geoUser.getLayerToUserReferences().add(ref);
        ref.setPermissions(Permission.MODE_WRITE);
        
        getDao().add(layer);

        AddnsRating r = new AddnsRating();
        r.setGeoLayer(layer);
        serviceRegistry.getRatingDao().add(r, true);

        return layer;
    }
    
    public static class Permission{
        
        public static final int MODE_READ = 0;
        public static final int MODE_WRITE = 1;
        
        private Long id;
        private String name;
        private Integer permission;
        private String type;
        
        //==========================
        
        public Permission(){}
        
        public Permission(Long id, String name, Integer permission, String type) {
            this.id = id;
            this.name = name;
            this.permission = permission;
            this.type = type;
        }
        
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getPermission() {
            return permission;
        }
        public void setPermission(Integer permission) {
            this.permission = permission;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }

    
    public static class PermissionForm{
        private Long id;
        List<Permission> permission;
        
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public List<Permission> getPermission() {
            return permission;
        }
        public void setPermission(List<Permission> permission) {
            this.permission = permission;
        }
    }


    @RequestMapping(value="/permissions/{id}", method = RequestMethod.GET)
    @Transactional(readOnly=true)
    public String getPermissionsById(@PathVariable Long id, Model model){
        GeoLayer layer = getById(id);

        List<Permission> u1 = new ArrayList<Permission>();
        Set<GeoLayerToUserReference> userRefSet = layer.getLayerToUserReferences();
        for (GeoLayerToUserReference userRef : userRefSet) {
            u1.add(
                    new Permission(
                            userRef.getGeoUser().getId(), 
                            userRef.getGeoUser().getLogin(), 
                            userRef.getPermissions(), 
                            "user"));
        }

        List<Permission> r1 = new ArrayList<Permission>();
        Set<GeoLayerToRoleReference> roleRefSet = layer.getLayerToRoleReferences();
        for (GeoLayerToRoleReference roleRef : roleRefSet) {
            r1.add(
                    new Permission(
                            roleRef.getGeoUserRole().getId(), 
                            roleRef.getGeoUserRole().getName(), 
                            roleRef.getPermissions(), 
                            "role"));
        }

        model.addAttribute("layer", layer);
        model.addAttribute("layerUsers", u1);
        model.addAttribute("layerRoles", r1);
        model.addAttribute("allUsers", serviceRegistry.getUserDao().list());
        model.addAttribute("allRoles", serviceRegistry.getUserRoleDao().list());

        return "markup/layer/Permissions";
    }
    
    
    @RequestMapping(value="/permissions/update", method = RequestMethod.POST)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#permission.id)")
    public @ResponseBody Long updatePermissions(@ModelAttribute PermissionForm permission){
        
        GeoLayer layer = getById(permission.getId());
        
        List<GeoUser> formUsers = new ArrayList<GeoUser>();
        List<GeoUserRole> formRoles = new ArrayList<GeoUserRole>();

        Map<GeoUser, GeoLayerToUserReference> indxUsers = new HashMap<GeoUser, GeoLayerToUserReference>();
        Map<GeoUserRole, GeoLayerToRoleReference> indxRoles = new HashMap<GeoUserRole, GeoLayerToRoleReference>();
        Map<String, Permission> indxPermissions = new HashMap<String, Permission>();
        
        // get assigned users and roles
        List<Permission> permissions = permission.getPermission();
        if(null != permissions){
            for (Permission p2 : permissions) {
                if(null == p2.getId()) 
                    continue;

                indxPermissions.put(p2.getType() + p2.getId(), p2);

                if("user".equals(p2.getType())){
                    formUsers.add(serviceRegistry.getUserDao().get(p2.getId()));
                }else{
                    formRoles.add(serviceRegistry.getUserRoleDao().get(p2.getId()));
                }
            }
        }
        
        // create indexes
        Set<GeoLayerToUserReference> refList1 = layer.getLayerToUserReferences();
        for (GeoLayerToUserReference ref1 : refList1) {
            indxUsers.put(ref1.getGeoUser(), ref1);
        }
        
         Set<GeoLayerToRoleReference> refList2 = layer.getLayerToRoleReferences();
        for (GeoLayerToRoleReference ref2 : refList2) {
            indxRoles.put(ref2.getGeoUserRole(), ref2);
        }
        
        // make diff by users
        List<GeoUser> updatedUsers = new ArrayList<GeoUser>();
        List<GeoUser> deletedUsers = new ArrayList<GeoUser>();
        List<GeoUser> addedUsers = new ArrayList<GeoUser>();
        
        List<GeoUser> allUsers = serviceRegistry.getUserDao().list();
        for (GeoUser geoUser : allUsers) {
            if(formUsers.contains(geoUser) && indxUsers.containsKey(geoUser)){
                updatedUsers.add(geoUser);
            }else if(! formUsers.contains(geoUser) && indxUsers.containsKey(geoUser)){
                deletedUsers.add(geoUser);
            }else if(formUsers.contains(geoUser)){
                addedUsers.add(geoUser);
            }
        }
        
        // update users 
        for (GeoUser geoUser0 : updatedUsers) {
            GeoLayerToUserReference ref3 = indxUsers.get(geoUser0);
            ref3.setPermissions(indxPermissions.get("user" + geoUser0.getId()).getPermission());
        }

        // delete users 
        for (GeoUser geoUser1 : deletedUsers) {
            GeoLayerToUserReference ref4 = indxUsers.get(geoUser1);
            layer.getLayerToUserReferences().remove(ref4);
        }
        
        // add users 
        for (GeoUser geoUser2 : addedUsers) {
            GeoLayerToUserReference ref5 = new GeoLayerToUserReference(layer, geoUser2);
            layer.getLayerToUserReferences().add(ref5);
            geoUser2.getLayerToUserReferences().add(ref5);
            ref5.setPermissions(indxPermissions.get("user" + geoUser2.getId()).getPermission());
        }
        

        // make diff by roles
        List<GeoUserRole> updatedRoles = new ArrayList<GeoUserRole>();
        List<GeoUserRole> deletedRoles = new ArrayList<GeoUserRole>();
        List<GeoUserRole> addedRoles = new ArrayList<GeoUserRole>();

        List<GeoUserRole> allRoles = serviceRegistry.getUserRoleDao().list();
        for (GeoUserRole geoRole : allRoles) {
            if(formRoles.contains(geoRole) && indxRoles.containsKey(geoRole)){
                updatedRoles.add(geoRole);
            }else if(! formRoles.contains(geoRole) && indxRoles.containsKey(geoRole)){
                deletedRoles.add(geoRole);
            }else if(formRoles.contains(geoRole)){
                addedRoles.add(geoRole);
            }
        }        
        
        // update roles 
        for (GeoUserRole geoRole0 : updatedRoles) {
            GeoLayerToRoleReference ref6 = indxRoles.get(geoRole0);
            ref6.setPermissions(indxPermissions.get("role" + geoRole0.getId()).getPermission());
        }

        // delete roles 
        for (GeoUserRole geoRole1 : deletedRoles) {
            GeoLayerToRoleReference ref7 = indxRoles.get(geoRole1);
            layer.getLayerToRoleReferences().remove(ref7);
        }
        
        // add roles 
        for (GeoUserRole geoRole2 : addedRoles) {
            GeoLayerToRoleReference ref8 = new GeoLayerToRoleReference(layer, geoRole2);
            layer.getLayerToRoleReferences().add(ref8);
            geoRole2.getLayerToRoleReferences().add(ref8);
            ref8.setPermissions(indxPermissions.get("role" + geoRole2.getId()).getPermission());
        }
        
        
        updateByObject(layer);
        
        indxUsers.clear();
        indxRoles.clear();
        indxPermissions.clear();
        
        return layer.getId();
    }
    
    
    @RequestMapping(value = "/tag/name", method = RequestMethod.GET)
    public @ResponseBody
    List<String>  getTagNames(@RequestParam("q") String q){
        return serviceRegistry.getGeoObjectDao().getTagsByPartialName(q);
    }
    
    @RequestMapping(value="/setLike", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody Boolean setLike(@RequestParam Long id, @RequestParam Long likes){
    	AddnsRating r = serviceRegistry.getRatingDao().get(id);
        if(null != r){
         	r.setLikes((likes + r.getLikes())/2);
        }else{
        	GeoLayer layer = serviceRegistry.getLayerDao().get(id);
        	r = new AddnsRating();
 	        r.setGeoLayer(layer);
 	        r.setLikes(likes);
         }
        serviceRegistry.getRatingDao().update(r, true);
    	return true;
    }
    
    @Cacheable("layerIconPermanent")
    @RequestMapping(value = "/treeicon/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @Transactional
    @ResponseBody
    public byte[] getTreeImage(@PathVariable Long id) {
        GeoLayer layer = serviceRegistry.getLayerDao().get(id);
        try{
        	Blob imgString = layer.getTreeIcon();
        	if (imgString == null){
        		//return default icon by type
        		return getDefaultTreeIcon(layer.getTypeId());
        	}
            BufferedImage overlay = ImageIO.read(imgString.getBinaryStream());
            int w = 16;
            int h = 16;
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
        }catch(Exception e){
        	logger.error(e.getLocalizedMessage(), e);
        	return getDefaultTreeIcon(layer.getTypeId());
        }
    }

    @Cacheable("layerIconPermanent")
    @RequestMapping(value = "/treeicon/", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getFlagImageDefault() {
    	return getDefaultTreeIcon(1L);
    }
    

    private byte[] getDefaultTreeIcon(Long typeId) {
    	try {
    		String imagePath = "/css/themes/classic/vector.png";
    		switch (typeId.intValue()) {
			case 1:
				imagePath = "/css/themes/classic/vector.png";
				break;
			case 2:
				imagePath = "/css/themes/classic/wms.png";
				break;
			case 3:
				imagePath = "/css/themes/classic/tile.png";
				break;
			case 5:
				imagePath = "/css/themes/classic/esri.png";
				break;
			}
    		BufferedImage image = ImageIO.read(new File(serviceRegistry.getServletContext().getRealPath(imagePath)));
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
    
    @CacheEvict(value = "layerIconPermanent", allEntries=true)
    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/uploadTreeIcon", method = RequestMethod.POST)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerUI.id, #layerUI.parentId)")
    public @ResponseBody Long uploadTreeIcon( 
            @ModelAttribute GeoLayerUIAdapter layerUI,
            @RequestParam MultipartFile treefile) throws IOException{

        GeoLayer l = null;

        if(null == layerUI.getId()){
            l = createGeoLayer(layerUI, serviceRegistry.getUserDao().getCurrentGeoUser());
        }else{
            l = getDao().get(layerUI.getId());
        }

        Blob blob = Hibernate.createBlob(treefile.getInputStream());
        l.setTreeIcon(blob);
        getDao().update(l, true);
        //serviceRegistry.getWms().resetLayer(l.getId());
        return l.getId();
    }
    
    @CacheEvict(value = "layerIconPermanent", allEntries=true)
    @RequestMapping(value = "/removeTreeIcon", method = RequestMethod.POST)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerUI.id, #layerUI.parentId)")
    public @ResponseBody Long removeTreeIcon( 
            @ModelAttribute GeoLayerUIAdapter layerUI) throws IOException{

        GeoLayer l = null;

        if(null == layerUI.getId()){
            return null;
        }else{
            l = getDao().get(layerUI.getId());
        }

        //Blob blob = Hibernate.createBlob(treefile.getInputStream());
        l.setTreeIcon(null);
        getDao().update(l, true);
        //serviceRegistry.getWms().resetLayer(l.getId());
        return l.getId();
    }
    
}
