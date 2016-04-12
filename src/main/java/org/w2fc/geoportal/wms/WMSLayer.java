package org.w2fc.geoportal.wms;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.gis.GeoObjectUIAdapter;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.CoordinateTransformer;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;

@Controller
@RequestMapping(value = "/wms")
public class WMSLayer {

	final Logger logger = LoggerFactory.getLogger(WMSLayer.class);

	@Autowired
	private WMSService wms;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private ServiceRegistry serviceRegistry;
	
	@Autowired
	private CoordinateTransformer transformer;
	
	@CacheEvict(value="WMSCache", allEntries = true)
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	@ResponseBody
	public boolean resetLayer(@RequestParam(value = "LAYERS") String layers){
		String[] layerIds = layers.split(",");
		for (int i = 0; i < layerIds.length; i++) {
			Long layerId = Long.valueOf(layerIds[i]);
			wms.resetLayer(layerId);
		}
		return true;
	}
	
	@RequestMapping(value = "/identify/", method = RequestMethod.GET)
	@Transactional
	 public @ResponseBody
	 	GeoObjectUIAdapter getIdentify(
	    		@RequestParam(value = "query_layers") String layers,
	    		@RequestParam(value = "lat") Double lat,
				@RequestParam(value = "lng") Double lng,
				@RequestParam(value = "SRS") String srs
	    		){
		//http://localhost:8080/zemresurs76/wms/identify/?service=WMS&request=GetFeatureInfo&version=1.1.1&layers=2&styles=&
		//format=image%2Fjpeg&transparent=true&width=1680&height=468&srs=EPSG%3A3857&
		//bbox=4275581.6141596185%2C7854821.900641282%2C4532410.029197811%2C7926366.959116207&query_layers=2&X=555&Y=263
		
		ParamsContainer params = new ParamsContainer();
		params.maxx = Double.valueOf(lat);
		params.maxy = Double.valueOf(lng);
		params.minx = 0;
		params.miny = 0;
		try{
			if(srs.equalsIgnoreCase("EPSG:3857")){
				transformer.toWGS(params, srs);
				lat = params.maxx;
				lng = params.maxy;
			}
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(), e);
		}
		ArrayList<Long> layerList = new ArrayList<Long>();
		layerList.add(Long.valueOf(layers));
		List<GeoObject> objects = serviceRegistry.getGeoObjectDao().getByPointAndLayers(lat, lng, layerList);
		if(objects.isEmpty() /*size() == 0*/){
			return null;
		}
		GeoObjectUIAdapter obj = new GeoObjectUIAdapter(objects.get(0));
		List<Map<String, String>> tags = obj.getTags();
		Map<String,String> area = new HashMap();
		area.put("id", "");
		area.put("key", "oarea");
		area.put("value", serviceRegistry.getGeoObjectDao().getArea(objects.get(0).getId()).toString());
		tags.add(area );
		return obj;
	}
	
	//@Cacheable(value="WMSCache", key="#layers.concat(#height).concat(#width).concat(#bbox)")
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getLayer(@RequestParam(value = "BBOX") String bbox,
			@RequestParam(value = "LAYERS") String layers,
			@RequestParam(value = "HEIGHT") Integer height,
			@RequestParam(value = "WIDTH") Integer width,
			@RequestParam(value = "SRS") String srs) throws ServletException {
		/*
		 * http://10.144.2.12:6080/arcgis/services/es_1_2/MapServer/WMSServer?
		 * SERVICE=WMS& REQUEST=GetMap& VERSION=1.1.1& LAYERS=6%2C7& STYLES=&
		 * FORMAT=image%2Fpng& TRANSPARENT=true& HEIGHT=256& WIDTH=256&
		 * SRS=EPSG%3A3857&
		 * BBOX=4451692.527328664,7890747.3039353145,4456584.497138916
		 * ,7895639.273745567
		 * 
		 * EPSG:3857
		 */
		logger.debug("Staring generate tile");
		MapContent map = new MapContent();
		map.setTitle("Quickstart");
		/*try {
			map.getViewport().setCoordinateReferenceSystem(CRS.decode(srs));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}*/
		String[] layerIds = layers.split(",");
		try {
			
			ParamsContainer params = new ParamsContainer();
			String[] box = bbox.split(",");
			// BBOX=minx,miny,maxx,maxy
			params.maxx = Double.valueOf(box[2]);
			params.maxy = Double.valueOf(box[3]);
			params.minx = Double.valueOf(box[0]);
			params.miny = Double.valueOf(box[1]);

			try {
				if(srs.equalsIgnoreCase("EPSG:3857")){
					transformer.toWGS(params, srs);
				}
			} catch ( Exception e ) {
				logger.error(e.getLocalizedMessage(), e);
			}
			
			params.height = height;
			params.width = width;
			//Float[] zoom = new Float[]{new Float(params.maxx - params.minx), new Float(params.maxy - params.miny)};
			
			for (int i = 0; i < layerIds.length; i++) {
				Long layerId = Long.valueOf(layerIds[i]);
				List<Layer> geoLayers = wms.getWmsLayers(layerId, servletContext, params);
				for(Layer l: geoLayers){
					map.addLayer(l);
				}
			}

			logger.debug("Preparing image to out");
			return createImage(map, params);
		} finally {
			map.dispose();
		}
	}

	
	@RequestMapping(value = "/permObj", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getPermissionArea(@RequestParam(value = "BBOX") String bbox,
			//@RequestParam(value = "LAYERS") String layers,
			@RequestParam(value = "HEIGHT") Integer height,
			@RequestParam(value = "WIDTH") Integer width)
			throws ServletException {

		logger.debug("Staring generate area tile");
		MapContent map = new MapContent();
		map.setTitle("Permission Area");
		
		//Long objId = Long.valueOf(layers);
		/*GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
		List<GeoObject> permobjects = new ArrayList<GeoObject>();
		for(GeoUserRole role : user.getGeoUserRoles()){
			for(GeoACL acl :role.getGeoACLs()){
				permobjects.add(acl.getGeoObject());
			}
		}
		for(GeoACL acl :user.getGeoACLs()){
			permobjects.add(acl.getGeoObject());
		}*/
		Geometry permArea = null;
		Geometry permAreaIslands = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object user = auth.getPrincipal();
		if (user instanceof CustomUserDetails) {
			permArea = ((CustomUserDetails) user).getPermissionArea().get( "shell");
			permAreaIslands = ((CustomUserDetails) user).getPermissionArea().get("islands");
		}

		map.addLayers(wms.getAreaObject(permArea, permAreaIslands, servletContext));
		try {
			ParamsContainer params = new ParamsContainer();
			String[] box = bbox.split(",");
			// BBOX=minx,miny,maxx,maxy
			params.maxx = Double.valueOf(box[2]);
			params.maxy = Double.valueOf(box[3]);
			params.minx = Double.valueOf(box[0]);
			params.miny = Double.valueOf(box[1]);

			params.height = height;
			params.width = width;
			logger.debug("Preparing area image to out");
			return createImage(map, params);
		} finally {
			map.dispose();
		}
	}

	private byte[] createImage(final MapContent map,
			ParamsContainer paramsContainer) throws ServletException {
		GTRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);
		Rectangle imageBounds = null;
		ReferencedEnvelope mapBounds = new ReferencedEnvelope();
		try { //
			mapBounds = map.getMaxBounds();
			mapBounds.init(
					paramsContainer.minx,
					paramsContainer.maxx,
					paramsContainer.miny,
					paramsContainer.maxy);

			imageBounds = new Rectangle(
					0, 
					0, 
					paramsContainer.width,
					paramsContainer.height);
			
		} catch (Exception e) { // failed to access map layers
			throw new ServletException(e);
		}
		BufferedImage image = new BufferedImage(
				imageBounds.width,
				imageBounds.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = image.createGraphics();
		try {
			renderer.paint(gr, imageBounds, mapBounds);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", new MemoryCacheImageOutputStream(baos));
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException e) {
			throw new ServletException(e);
		}

	}

}
