package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.ws.exception.IllegalGeometryTypeException;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

/**
 * @author yevhenlozov
 */
public class ByTypeGeometryParameterFactory implements GeometryParameterFactory {

    private RequestGeoObject requestGeoObject;

    public ByTypeGeometryParameterFactory(RequestGeoObject requestGeoObject) {
        this.requestGeoObject = requestGeoObject;
    }

    @Override
    public GeometryParameter create() {
        GeometryParameter geometryParameter;

        switch (requestGeoObject.getType()){
            case POINT:
                geometryParameter = new RequestPointFactory(requestGeoObject).create();
                break;
            case LINESTRING:
                geometryParameter = new RequestLineFactory(requestGeoObject).create();
                break;
            case POLYGON:
                geometryParameter = new RequestPolygonFactory(requestGeoObject).create();
                break;
            case MULTIPOINT:
                geometryParameter = new MultiPointFactory(requestGeoObject).create();
                break;
            case MULTILINESTRING:
                geometryParameter = new MultiLineStringFactory(requestGeoObject).create();
                break;
            case MULTIPOLYGON:
                geometryParameter = new MultiPolygonFactory(requestGeoObject).create();
                break;
            default:
                throw new IllegalGeometryTypeException("Geometry type is not allowed: " + requestGeoObject.getType());
        }

        return geometryParameter;
    }
}
