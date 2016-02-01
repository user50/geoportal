package org.w2fc.geoportal.ws.geometry.builder;

import org.w2fc.geoportal.dao.ReferenceSystemProjDao;
import org.w2fc.geoportal.ws.exception.IllegalGeometryTypeException;
import org.w2fc.geoportal.ws.geocoder.GeoCoder;
import org.w2fc.geoportal.ws.model.GeometryParameter;

/**
 * @author yevhenlozov
 */
public class GeometryBuilderFactory {

    private GeoCoder geoCoder;
    private ReferenceSystemProjDao referenceSystemDao;

    public GeometryBuilderFactory(GeoCoder geoCoder, ReferenceSystemProjDao referenceSystemDao) {
        this.geoCoder = geoCoder;
        this.referenceSystemDao = referenceSystemDao;
    }

    public GeometryBuilder create(GeometryParameter geometryParameter) {
        if (geometryParameter.getWkt() != null && !geometryParameter.getWkt().isEmpty())
            return new TransformCoordinate(new WKTGeometryBuilder(), referenceSystemDao);

        GeometryBuilder geometryBuilder;

        switch (geometryParameter.getType()){
            case POINT:
                geometryBuilder = new TransformCoordinate(new PointGeometryBuilder(geoCoder), referenceSystemDao);
                break;
            case LINESTRING:
                geometryBuilder = new TransformCoordinate(new LineGeometryBuilder(), referenceSystemDao);
                break;
            case POLYGON:
                geometryBuilder = new TransformCoordinate(new PolygonGeometryBuilder(), referenceSystemDao);
                break;
            case MULTIPOINT:
                geometryBuilder = new TransformCoordinate(new MultiPointGeometryBuilder(), referenceSystemDao);
                break;
            case MULTILINESTRING:
                geometryBuilder = new TransformCoordinate(new MultiLineStringGeometryBuilder(), referenceSystemDao);
                break;
            case MULTIPOLYGON:
                geometryBuilder = new TransformCoordinate(new MultiPolygonBuilder(), referenceSystemDao);
                break;
            default:
                throw new IllegalGeometryTypeException("Geometry type is not allowed: " + geometryParameter.getType());
        }

        return geometryBuilder;
    }
}
