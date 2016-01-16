package org.w2fc.geoportal.wms;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
	@Cacheable(value="WMSCache", key="#layers.concat(#height).concat(#width).concat(#bbox)")
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
			for (int i = 0; i < layerIds.length; i++) {
				Long layerId = Long.valueOf(layerIds[i]);
				List<Layer> geoLayers = wms.getWmsLayers(layerId, servletContext);
				for(Layer l: geoLayers){
					map.addLayer(l);
				}		
			}
			ParamsContainer params = new ParamsContainer();
			String[] box = bbox.split(",");
			// BBOX=minx,miny,maxx,maxy
			params.maxx = Double.valueOf(box[2]);
			params.maxy = Double.valueOf(box[3]);
			params.minx = Double.valueOf(box[0]);
			params.miny = Double.valueOf(box[1]);

			try{
				if(srs.equalsIgnoreCase("EPSG:3857")){
					transformer.toWGS(params, srs);
				}
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(), e);
			}
			
			params.height = height;
			params.width = width;
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
    	if(user instanceof CustomUserDetails){
    		permArea = ((CustomUserDetails)user).getPermissionArea().get("shell");
    		permAreaIslands = ((CustomUserDetails)user).getPermissionArea().get("islands");
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
			mapBounds.init(paramsContainer.minx, paramsContainer.maxx,
					paramsContainer.miny, paramsContainer.maxy);
			imageBounds = new Rectangle(0, 0, paramsContainer.width,
					paramsContainer.height);
		} catch (Exception e) { // failed to access map layers
			throw new ServletException(e);
		}
		BufferedImage image = new BufferedImage(imageBounds.width,
				imageBounds.height, BufferedImage.TYPE_INT_ARGB);
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
