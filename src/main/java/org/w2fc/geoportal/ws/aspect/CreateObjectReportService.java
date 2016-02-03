package org.w2fc.geoportal.ws.aspect;

import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateObjectReportService {

    private static final Long LAYER_ID = 1L; // todo getting layer

    private OperationStatusRepository repository;
    private GeoUserDao geoUserDao;

    List<String> successions = new ArrayList<String>();
    Map<String,String> fails = new HashMap<String, String>();

    public CreateObjectReportService(OperationStatusRepository repository, GeoUserDao geoUserDao) {
        this.repository = repository;
        this.geoUserDao = geoUserDao;
    }

    public void collectSuccessCreatedObject(String guid) {
        successions.add(guid);
    }

    public void collectCreationFailedObject(String requestGeoObjectGuid, String errorMessage) {
        fails.put(requestGeoObjectGuid, errorMessage);
    }

    public void saveReport() {
        //TODO
    }
}
