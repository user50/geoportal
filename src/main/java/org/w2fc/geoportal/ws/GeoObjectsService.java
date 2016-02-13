package org.w2fc.geoportal.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.ws.async.AsyncService;
import org.w2fc.geoportal.ws.async.SOAPProcessStatus;
import org.w2fc.geoportal.ws.model.*;

import java.util.*;

@Service
public class GeoObjectsService {

    final Logger logger = LoggerFactory.getLogger(GeoObjectsService.class);

    private AsyncService asyncService = new AsyncService();

    private GeoObjectService geoObjectService;

    @Autowired
    public GeoObjectsService(GeoObjectService geoObjectService) {
        this.geoObjectService = geoObjectService;
    }

    public GeoObjectsService() {
    }

    public String createObjects(final List<RequestGeoObject> geoObjectsReq){
        Runnable runnable = new Runnable() {
            public void run() {
                for (RequestGeoObject requestGeoObject : geoObjectsReq) {
                    geoObjectService.createAndSaveObject(requestGeoObject);
                }
            }
        };
        return asyncService.asyncExecute(runnable);
    }

    public String updateObjects(final List<RequestGeoObject> geoObjectsReq) {
        Runnable runnable = new Runnable() {
            public void run() {
                for (RequestGeoObject requestGeoObject : geoObjectsReq) {
                    geoObjectService.updateObject(requestGeoObject);
                }
            }
        };
        return asyncService.asyncExecute(runnable);
    }

    public String deleteObjects(final List<Long> ids){
        Runnable runnable = new Runnable() {
            public void run() {
                for (Long id : ids) {
                    geoObjectService.delete(id);
                }
            }
        };

        return asyncService.asyncExecute(runnable);
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

    public List<String> getSpatialRefSystems(){
       return geoObjectService.getSpatialRefSystems();
    }
}
