package org.w2fc.geoportal.wms;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;

import org.geotools.map.Layer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Geometry;

@Service
public class WMSService {
	
	@Autowired
	private GeoProcessor geoProcessor;

	@Autowired
	private AreaProcessor areaProcessor;
	
	public boolean resetLayers(String layers) {
		String[] layerIds = layers.split(",");
		for (int i = 0; i < layerIds.length; i++) {
			resetLayer(Long.valueOf(layerIds[i]));
		}
		return true;
	}

	//@CacheEvict(value="GeoPortalCache", key="#layerId")
	@CacheEvict(value="GeoPortalCache", allEntries=true)
	public boolean resetLayer(Long layerId) {
		return true;
	}

	public List<Layer> getWmsLayers(Long layerId, ServletContext servletContext, ParamsContainer params) {
		return geoProcessor.getWmsLayers(layerId, servletContext, params);
	}
	
	public Collection<Layer> getAreaObject(Geometry permobjects, Geometry permAreaIslands, ServletContext servletContext) {
		return areaProcessor.getAreaObject(permobjects, permAreaIslands, servletContext);
	}

}
