package org.w2fc.geoportal.ws.geometry;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.MultiPoint;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.RequestGeoObject;
import org.w2fc.geoportal.ws.model.RequestLine;

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
        Long layerId = reqGeoObject.getLayerId();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
        List<PointCoordinates> pointCoordinates = new PointCoordinatesFromJsonFactory().create(jsonCoordsArray);

        MultiPoint multiPoint = new MultiPoint(name, layerId, pointCoordinates.toArray(new PointCoordinates[pointCoordinates.size()]));
        multiPoint.setWkt(wkt);
        multiPoint.setTags(tags);

        return multiPoint;

    }
}
