package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.RequestGeoObject;
import org.w2fc.geoportal.ws.model.RequestPoint;

import java.util.List;
import java.util.Set;

/**
 * @author yevhenlozov
 */
public class RequestPointFactory implements GeometryParameterFactory {

    private RequestGeoObject reqGeoObject;

    public RequestPointFactory(RequestGeoObject requestGeoObject) {
        this.reqGeoObject = requestGeoObject;
    }

    @Override
    public RequestPoint create() {
        String name = reqGeoObject.getName();
        Long layerId = reqGeoObject.getLayerId();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();
        String reKey = reqGeoObject.getSpatialKey();
        String guid = reqGeoObject.getGuid();
        String extSysId = reqGeoObject.getExtSysId();

        RequestPoint requestPoint;
        if (reqGeoObject.getPointsCoordinates() == null){
            String address = reqGeoObject.getAddress();
            requestPoint = new RequestPoint(name, layerId, address);
        } else  {
            String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
            List<PointCoordinates> pointCoordinates = new PointCoordinatesFromJsonFactory().create(jsonCoordsArray);
            requestPoint = new RequestPoint(name, layerId, pointCoordinates.get(0));
        }

        requestPoint.setWkt(wkt);
        requestPoint.setTags(tags);
        requestPoint.setSpatialKey(reKey);
        requestPoint.setGuid(guid);
        requestPoint.setExtSysId(extSysId);

        return requestPoint;
    }
}
