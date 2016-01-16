package org.w2fc.geoportal.wms;

import java.util.List;

import javax.servlet.ServletContext;

import org.geotools.map.Layer;

public interface GeoProcessor {
	public List<Layer> getWmsLayers(Long layerId, ServletContext servletContext);
}
