package org.w2fc.geoportal.utils;

import org.hibernate.search.bridge.TwoWayStringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryFieldBridge implements TwoWayStringBridge{

	final Logger logger = LoggerFactory.getLogger(GeometryFieldBridge.class);
	
	@Override
	public String objectToString(Object geom) {
		if(geom instanceof Geometry){
			return ((Geometry)geom).toText();
		}
		return null;
	}

	@Override
	public Object stringToObject(String wkt) {
		try {
			Geometry g = new WKTReader().read(wkt);
			return g;
		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
