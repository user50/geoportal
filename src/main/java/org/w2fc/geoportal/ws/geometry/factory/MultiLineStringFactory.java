package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.*;

public class MultiLineStringFactory implements GeometryParameterFactory {
    private RequestGeoObject reqGeoObject;

    public MultiLineStringFactory(RequestGeoObject reqGeoObject) {
        this.reqGeoObject = reqGeoObject;
    }

    @Override
    public GeometryParameter create() {
        return null;
    }
}
