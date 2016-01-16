package org.w2fc.geoportal.utils;

import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.wms.ParamsContainer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@Service
public class CoordinateTransformer {

	final static Logger logger = LoggerFactory.getLogger(CoordinateTransformer.class);

	private static final String WGS84 = "WGS84";

	@Autowired
	protected ServiceRegistry serviceRegistry;

	private CoordinateReferenceSystem targetSystem;
	
	
	static{
		System.setProperty("org.geotools.referencing.forceXY", "true");
	}

	public CoordinateTransformer() {
		try {
			targetSystem = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			targetSystem = null;
		}
	}

	public List<GeoObject> toWGS(List<GeoObject> list, String refKey) throws Exception {
		if(WGS84.equals(refKey))return list;
		CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(refKey).getWkt());
		MathTransform transform = CRS.findMathTransform(sourceSystem, targetSystem, true);
		for(GeoObject obj : list){
			Geometry targetGeometry = JTS.transform(obj.getTheGeom(), transform);
			obj.setTheGeom(targetGeometry);
		}	
		return list;
	}
	
	public void toWGS(ParamsContainer params, String refKey) throws Exception {
		if(WGS84.equals(refKey))return;
		CoordinateReferenceSystem sourceSystem = CRS.decode(refKey);
		MathTransform transform = CRS.findMathTransform(sourceSystem, targetSystem, true);
		Coordinate sw = new Coordinate(params.maxx,params.maxy);
		Coordinate ne = new Coordinate(params.minx,params.miny);
		Coordinate sw_new  = JTS.transform(sw, null, transform);
		Coordinate ne_new  = JTS.transform(ne, null, transform);
		params.maxx = sw_new.x;
		params.maxy = sw_new.y;
		params.minx = ne_new.x;
		params.miny = ne_new.y;
		return;
	}

	public List<GeoObject> toSRS(List<GeoObject> list, String refKey) throws Exception{
		if(WGS84.equals(refKey))return list;
		CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(refKey).getWkt());
		MathTransform transform = CRS.findMathTransform(targetSystem, sourceSystem, true);
		for(GeoObject obj : list){
			Geometry targetGeometry = JTS.transform(obj.getTheGeom(), transform);
			obj.setTheGeom(targetGeometry);
		}	
		return list;
	}


	
/*	public static List<GeoObject> transformFrom(String coordinateSystem,
			List<GeoObject> listByLayerId) {
		if(systems.get(coordinateSystem) == null)return listByLayerId;
		try {
			MathTransform transform = CRS.findMathTransform(systems.get(coordinateSystem), systems.get("WGS84"), true);
			for(GeoObject obj : listByLayerId){
				Geometry targetGeometry = JTS.transform(obj.getTheGeom(), transform);
				obj.setTheGeom(targetGeometry);
			}			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		return listByLayerId;
	}*/


}
