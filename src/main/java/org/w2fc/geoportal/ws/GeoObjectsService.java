package org.w2fc.geoportal.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.ws.model.*;

import java.util.*;

@Service
public class GeoObjectsService {

    final Logger logger = LoggerFactory.getLogger(GeoObjectsService.class);

    private GeoObjectService geoObjectService;

    @Autowired
    public GeoObjectsService(GeoObjectService geoObjectService) {
        this.geoObjectService = geoObjectService;
    }

    public GeoObjectsService() {
    }

    public void createObjects(List<RequestGeoObject> geoObjectsReq){
        for (RequestGeoObject requestGeoObject : geoObjectsReq) {
            geoObjectService.createAndSaveObject(requestGeoObject);
        }
    }

    public void updateObjects(List<RequestGeoObject> geoObjectsReq) {
        for (RequestGeoObject requestGeoObject : geoObjectsReq) {
            geoObjectService.updateObject(requestGeoObject);
        }
    }

    public void deleteObjects(List<Long> ids){
        for (Long id : ids) {
            geoObjectService.delete(id);
        }
    }

    public Long createPoint(RequestPoint rp)
    {
        return geoObjectService.createAndSaveObject(rp);
    }

    public Long createLine(RequestLine rp)
    {
        return geoObjectService.createAndSaveObject(rp);
    }

    public Long createPolygon(RequestPolygon rp)
    {
        return geoObjectService.createAndSaveObject(rp);
    }

    public void updatePoint(Long id, RequestPoint request)
    {
        geoObjectService.updateObject(id, request);
    }

    public void updateLine(Long id, RequestLine request)
    {
        geoObjectService.updateObject(id, request);
    }

    public void updatePolygon(Long id, RequestPolygon request)
    {
        geoObjectService.updateObject(id, request);
    }
}
