package org.w2fc.geoportal.ws.geometry.builder;

import org.w2fc.geoportal.ws.exception.IllegalGeometryTypeException;
import org.w2fc.geoportal.ws.geocoder.GeoCoder;
import org.w2fc.geoportal.ws.model.GeometryParameter;

/**
 * @author yevhenlozov
 */
public class GeometryBuilderFactory {

    private GeoCoder geoCoder;

    public GeometryBuilderFactory(GeoCoder geoCoder) {
        this.geoCoder = geoCoder;
    }

    public GeometryBuilder create(GeometryParameter geometryParameter) {
        if (geometryParameter.getWkt() != null && !geometryParameter.getWkt().isEmpty())
            return new WKTGeometryBuilder();

        GeometryBuilder geometryBuilder;

        switch (geometryParameter.getType()){
            case POINT:
                geometryBuilder = new PointGeometryBuilder(geoCoder);
                break;
            case LINESTRING:
                geometryBuilder = new LineGeometryBuilder();
                break;
            case POLYGON:
                geometryBuilder = new PolygonGeometryBuilder();
                break;
            case MULTIPOINT:
                geometryBuilder = new MultiPointGeometryBuilder();
                break;
            case MULTILINESTRING:
                geometryBuilder = new MultiLineStringGeometryBuilder();
                break;
            case MULTIPOLYGON:
                geometryBuilder = new MultiPolygonBuilder();
                break;
            default:
                throw new IllegalGeometryTypeException("Geometry type is not allowed: " + geometryParameter.getType());
        }

        return geometryBuilder;
    }
}
