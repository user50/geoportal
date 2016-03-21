package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.*;

import java.util.List;
import java.util.Set;

public class MultiPolygonFactory implements GeometryParameterFactory {

    private RequestGeoObject reqGeoObject;

    public MultiPolygonFactory(RequestGeoObject reqGeoObject) {
        this.reqGeoObject = reqGeoObject;
    }

    @Override
    public GeometryParameter create() {
        String name = reqGeoObject.getName();
        Long layerId = reqGeoObject.getLayerId();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();
        String reKey = reqGeoObject.getSpatialKey();
        String guid = reqGeoObject.getGuid();
        String extSysId = reqGeoObject.getExtSysId();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();

        List<GeoPolygon> polygons = new GeoPolygonsFromJsonFactory().create(jsonCoordsArray);

        MultiPolygon multiPolygon = new MultiPolygon(name, layerId, polygons);
        multiPolygon.setTags(tags);
        multiPolygon.setWkt(wkt);
        multiPolygon.setSpatialKey(reKey);
        multiPolygon.setExtSysId(extSysId);
        multiPolygon.setGuid(guid);

        return multiPolygon;
    }
}
