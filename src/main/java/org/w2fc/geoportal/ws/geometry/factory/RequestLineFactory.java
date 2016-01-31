package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.RequestGeoObject;
import org.w2fc.geoportal.ws.model.RequestLine;

import java.util.List;
import java.util.Set;

/**
 * @author yevhenlozov
 */
public class RequestLineFactory implements GeometryParameterFactory {

    private RequestGeoObject reqGeoObject;

    public RequestLineFactory(RequestGeoObject requestGeoObject) {
        this.reqGeoObject = requestGeoObject;
    }

    @Override
    public RequestLine create() {
        String name = reqGeoObject.getName();
        Long layerId = reqGeoObject.getLayerId();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
        List<PointCoordinates> pointCoordinates = new PointCoordinatesFromJsonFactory().create(jsonCoordsArray);

        RequestLine requestLine = new RequestLine(name, layerId, pointCoordinates.toArray(new PointCoordinates[pointCoordinates.size()]));
        requestLine.setWkt(wkt);
        requestLine.setTags(tags);

        return requestLine;
    }
}
