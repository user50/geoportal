package org.w2fc.geoportal.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.auth.GeoportalSecurity;
import org.w2fc.geoportal.ws.async.AsyncService;
import org.w2fc.geoportal.ws.async.SOAPProcessStatus;
import org.w2fc.geoportal.ws.exception.AccessDeniedException;
import org.w2fc.geoportal.ws.model.*;

import java.util.*;

@Service
public class GeoObjectsService {

    final Logger logger = LoggerFactory.getLogger(GeoObjectsService.class);

    private AsyncService asyncService = new AsyncService();

    private GeoObjectService geoObjectService;

    GeoportalSecurity geoportalSecurity;

    @Autowired
    public GeoObjectsService(GeoObjectService geoObjectService, GeoportalSecurity geoportalSecurity) {
        this.geoObjectService = geoObjectService;
        this.geoportalSecurity = geoportalSecurity;
    }

    public GeoObjectsService() {
    }

    private void checkPermissions(final List<RequestGeoObject> geoObjectsReq)
    {
        Set<Long> layerIds = new HashSet<Long>();
        for (RequestGeoObject requestGeoObject : geoObjectsReq)
            layerIds.add(requestGeoObject.getLayerId());

        for (Long layerId : layerIds) {
            if (!geoportalSecurity.isLayerEditor(layerId))
                throw new AccessDeniedException("Current user has not access to layer "+layerId);
        }
    }

    public String createObjects(final List<RequestGeoObject> geoObjectsReq){
        checkPermissions(geoObjectsReq);

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
        checkPermissions(geoObjectsReq);

        Runnable runnable = new Runnable() {
            public void run() {
                for (RequestGeoObject requestGeoObject : geoObjectsReq) {
                    geoObjectService.updateObject(requestGeoObject);
                }
            }
        };
        return asyncService.asyncExecute(runnable);
    }

    public String deleteObjects(final String extSysId, final List<String> guids){
        Runnable runnable = new Runnable() {
            public void run() {
                for (String guid : guids) {
                    geoObjectService.delete(extSysId, guid);
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

    public void deleteObject(Long id){
        geoObjectService.delete(id);
    }
    public List<String> getSpatialRefSystems(){
       return geoObjectService.getSpatialRefSystems();
    }
}
