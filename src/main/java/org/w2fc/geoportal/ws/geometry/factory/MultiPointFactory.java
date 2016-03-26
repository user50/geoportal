package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.MultiPoint;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

import java.util.List;
import java.util.Set;

public class MultiPointFactory implements GeometryParameterFactory {

    private RequestGeoObject reqGeoObject;

    public MultiPointFactory(RequestGeoObject reqGeoObject) {
        this.reqGeoObject = reqGeoObject;
    }

    @Override
    public GeometryParameter create() {
        String name = reqGeoObject.getName();
        Set<Long> layerIds = reqGeoObject.getLayerIds();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();
        String reKey = reqGeoObject.getSpatialKey();
        String guid = reqGeoObject.getGuid();
        String extSysId = reqGeoObject.getExtSysId();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
        List<PointCoordinates> pointCoordinates = new PointCoordinatesFromJsonFactory().create(jsonCoordsArray);

        MultiPoint multiPoint = new MultiPoint(name, layerIds, pointCoordinates.toArray(new PointCoordinates[pointCoordinates.size()]));
        multiPoint.setWkt(wkt);
        multiPoint.setTags(tags);
        multiPoint.setSpatialKey(reKey);
        multiPoint.setExtSysId(extSysId);
        multiPoint.setGuid(guid);

        return multiPoint;

    }
}
