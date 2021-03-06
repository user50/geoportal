package org.w2fc.geoportal.ws.geometry.factory;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.LineCoordinates;
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
        Set<Long> layerIds = reqGeoObject.getLayerIds();
        String wkt = reqGeoObject.getWkt();
        Set<GeoObjectTag> tags = reqGeoObject.getTags();
        String reKey = reqGeoObject.getSpatialKey();
        String guid = reqGeoObject.getGuid();
        String extSysId = reqGeoObject.getExtSysId();

        String jsonCoordsArray = reqGeoObject.getPointsCoordinates();
        List<LineCoordinates> lines = new LinesCoordinatesFromJsonFactory().create(jsonCoordsArray);

        LineCoordinates externalPolygon = lines.get(0);

        RequestPolygon requestPolygon = new RequestPolygon(name, layerIds, externalPolygon.getPointsCoordinates());
        requestPolygon.setWkt(wkt);
        requestPolygon.setTags(tags);
        requestPolygon.setPolygonHoles(lines.subList(1, lines.size()).toArray(new LineCoordinates[lines.size() - 1]));
        requestPolygon.setSpatialKey(reKey);
        requestPolygon.setExtSysId(extSysId);
        requestPolygon.setGuid(guid);

        return requestPolygon;
    }
}
