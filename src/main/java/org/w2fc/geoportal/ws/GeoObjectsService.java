package org.w2fc.geoportal.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.auth.GeoportalSecurity;
import org.w2fc.geoportal.ws.async.AsyncService;
import org.w2fc.geoportal.ws.async.Task;
import org.w2fc.geoportal.ws.error.ErrorCodeProvider;
import org.w2fc.geoportal.ws.error.ErrorDesc;
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
            layerIds.addAll(requestGeoObject.getLayerIds());

        geoportalSecurity.checkLayersPermissions(layerIds);
    }

    private void checkPermissions(GeometryParameter params)
    {
        geoportalSecurity.checkLayersPermissions(params.getLayerIds());
    }

    public String createObjects(final List<RequestGeoObject> geoObjectsReq){
        checkPermissions(geoObjectsReq);

        final ErrorCodeProvider codeProvider = new ErrorCodeProvider();

        Task runnable = new Task() {
            public void run() {
                for (RequestGeoObject requestGeoObject : geoObjectsReq) {
                    try {
                        geoObjectService.createAndSaveObject(requestGeoObject);
                    } catch (Exception e) {
                        logger.error(e.getLocalizedMessage(), e);
                    	add(new ErrorDesc(codeProvider.getCode(e), requestGeoObject.getGuid()));
                    }
                }
            }
        };
        return asyncService.asyncExecute(runnable);
    }

    public String updateObjects(final List<RequestGeoObject> geoObjectsReq) {
        checkPermissions(geoObjectsReq);

        final ErrorCodeProvider codeProvider = new ErrorCodeProvider();

        Task runnable = new Task() {
            public void run() {
                for (RequestGeoObject requestGeoObject : geoObjectsReq) {
                    try {
                        geoObjectService.updateObject(requestGeoObject);
                    } catch (Exception e) {
                    	logger.error(e.getLocalizedMessage(), e);
                        add(new ErrorDesc(codeProvider.getCode(e), requestGeoObject.getGuid()));
                    }
                }
            }
        };
        return asyncService.asyncExecute(runnable);
    }

    public String deleteObjects(final String extSysId, final List<String> guids){

        final ErrorCodeProvider codeProvider = new ErrorCodeProvider();

        Task runnable = new Task() {
            public void run() {
                for (String guid : guids) {
                    try {
                        geoObjectService.delete(extSysId, guid);
                    } catch (Exception e) {
                    	logger.error(e.getLocalizedMessage(), e);
                        add(new ErrorDesc(codeProvider.getCode(e), guid));
                    }
                }
            }
        };

        return asyncService.asyncExecute(runnable);
    }

    public Long createPoint(RequestPoint rp)
    {
        checkPermissions(rp);
        return geoObjectService.createAndSaveObject(rp);
    }

    public Long createLine(RequestLine rp)
    {
        checkPermissions(rp);
        return geoObjectService.createAndSaveObject(rp);
    }

    public Long createPolygon(RequestPolygon rp)
    {
        checkPermissions(rp);
        return geoObjectService.createAndSaveObject(rp);
    }

    public void updatePoint(Long id, RequestPoint request)
    {
        checkPermissions(request);
        geoObjectService.updateObject(id, request);
    }

    public void updateLine(Long id, RequestLine request)
    {
        checkPermissions(request);
        geoObjectService.updateObject(id, request);
    }

    public void updatePolygon(Long id, RequestPolygon request)
    {
        checkPermissions(request);
        geoObjectService.updateObject(id, request);
    }

    public void deleteObject(Long id){
        geoObjectService.delete(id);
    }
    public List<String> getSpatialRefSystems(){
       return geoObjectService.getSpatialRefSystems();
    }
}
