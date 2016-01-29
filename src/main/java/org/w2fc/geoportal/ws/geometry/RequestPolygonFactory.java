package org.w2fc.geoportal.ws.geometry;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.PolygonHole;
import org.w2fc.geoportal.ws.model.RequestGeoObject;
import org.w2fc.geoportal.ws.model.RequestPolygon;

import java.util.List;
import java.util.Set;

/**
 * @author yevhenlozov
 */
public class RequestPolygonFactory implements GeometryParameterFactory {

    private RequestGeoObject reqGeoObject;

    public RequestPolygonFactory(RequestGeoObject requestGeoObject) {
        this.reqGeoObject = requestGeoObject;
    }

    @Override
    public RequestPolygon create() {
        String name = reqGeoObject.getName();
        Long layerId = reqGeoObject.getLayerId();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
        List<PointCoordinates> pointCoordinates = new PointCoordinatesFromJsonFactory().create(jsonCoordsArray);
        List<PolygonHole> polygonHoles = new PolygonHoleFromJsonFactory().create(jsonCoordsArray);

        RequestPolygon requestPolygon =new RequestPolygon(name, layerId, pointCoordinates.toArray(new PointCoordinates[pointCoordinates.size()]));
        requestPolygon.setWkt(wkt);
        requestPolygon.setTags(tags);
        requestPolygon.setPolygonHoles(polygonHoles.toArray(new PolygonHole[polygonHoles.size()]));

        return requestPolygon;
    }
}
