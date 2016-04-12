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
import org.w2fc.geoportal.domain.ReferenceSystemProj;
import org.w2fc.geoportal.wms.ParamsContainer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@Service
public class CoordinateTransformer {

	final static Logger logger = LoggerFactory.getLogger(CoordinateTransformer.class);

	public static final String WGS84 = "WGS84";

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
		for(int i = 0 ; i < list.size() ; i++){
			list.set(i, toWGS(list.get(i),refKey));
		}
		return list;
	}
	
	
	public GeoObject toWGS(GeoObject gisObject, String refKey) throws Exception{
		if(WGS84.equals(refKey))return gisObject;
		ReferenceSystemProj prj = serviceRegistry.getReferenceSystemProjDao().get(refKey);
		if(prj.getProvidedSpatialReference() != null){
			//Affine transform
			Double dx = prj.getDx();
			Double dy = prj.getDy();
			Double Q = prj.getQ();
			
			CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(prj.getProvidedSpatialReference()).getWkt());
			MathTransform transform = CRS.findMathTransform(sourceSystem, targetSystem, true);
			//for(GeoObject obj : list){
				Geometry geom = gisObject.getTheGeom();
				for(Coordinate c : geom.getCoordinates()){
					//X=x*cos(Q)-y*sin(Q)+dx
					//Y=x*sin(Q)+y*cos(Q)+dy
					double x = c.x*Math.cos(Q)-c.y*Math.sin(Q)+dx;
					double y = c.x*Math.sin(Q)+c.y*Math.cos(Q)+dy;
					c.x = x;
					c.y = y;
				}
				Geometry targetGeometry = JTS.transform(geom, transform);
				gisObject.setTheGeom(targetGeometry);
			//}
		}else{
			CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(refKey).getWkt());
			MathTransform transform = CRS.findMathTransform(sourceSystem, targetSystem, true);
			//for(GeoObject obj : list){
				Geometry targetGeometry = JTS.transform(gisObject.getTheGeom(), transform);
				gisObject.setTheGeom(targetGeometry);
			//}
		}
		return gisObject;
		
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

	public List<GeoObject> toSRS(List<GeoObject> list, String refKey) throws Exception {
		if (WGS84.equals(refKey))return list;
		ReferenceSystemProj prj = serviceRegistry.getReferenceSystemProjDao().get(refKey);
		if (prj.getProvidedSpatialReference() != null) {
			//Affine transform
			Double dx = prj.getDx();
			Double dy = prj.getDy();
			Double Q = prj.getQ();
			CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(prj.getProvidedSpatialReference()).getWkt());
			MathTransform transform = CRS.findMathTransform(targetSystem, sourceSystem, true);
			for (GeoObject obj : list) {
				Geometry targetGeometry = JTS.transform(obj.getTheGeom(), transform);
				for(Coordinate c : targetGeometry.getCoordinates()){
					//x=(X-dx)*cos(Q)+(Y-dY)*sin(Q)
					//y=-(X-dx)*sin(Q)+(Y-dy)*cos(Q)
					double x = (c.x-dx)*Math.cos(Q)+(c.y-dy)*Math.sin(Q);
					double y = -(c.x-dx)*Math.sin(Q)+(c.y-dy)*Math.cos(Q);
					c.x = x;
					c.y = y;
				}
				obj.setTheGeom(targetGeometry);
			}
		} else {
			CoordinateReferenceSystem sourceSystem = CRS.parseWKT(serviceRegistry.getReferenceSystemProjDao().get(refKey).getWkt());
			MathTransform transform = CRS.findMathTransform(targetSystem, sourceSystem, true);
			for (GeoObject obj : list) {
				Geometry targetGeometry = JTS.transform(obj.getTheGeom(), transform);
				obj.setTheGeom(targetGeometry);
			}
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
