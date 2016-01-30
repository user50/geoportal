package org.w2fc.geoportal.ws.geometry;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.*;

import java.util.List;
import java.util.Set;

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
