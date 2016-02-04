package org.w2fc.geoportal.ws.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.domain.OperationStatus;

import java.util.*;

public class CreateObjectReportService {
    final Logger logger = LoggerFactory.getLogger(ReportAspect.class);


    private static final Long LAYER_ID = 1L; // todo getting layer

    private OperationStatusRepository repository;
    private GeoUserDao geoUserDao;

    ThreadLocal<List<String>> successions = new ThreadLocal<List<String>>();
    ThreadLocal<Map<String,String>> fails = new ThreadLocal<Map<String, String>>();

    public CreateObjectReportService(OperationStatusRepository repository, GeoUserDao geoUserDao) {
        this.repository = repository;
        this.geoUserDao = geoUserDao;
    }

    public void init() {
        successions.set(new ArrayList<String>());
        fails.set(new HashMap<String, String>());
    }

    public void collectSuccessCreatedObject(String guid) {

        successions.get().add(guid);
    }

    public void collectFailedCreatedObject(String requestGeoObjectGuid, String errorMessage) {
        fails.get().put(requestGeoObjectGuid, errorMessage);
    }

    public void saveReport() {
        Long userId = geoUserDao.getCurrentGeoUser().getId();
        OperationStatus.Status status = fails.get().isEmpty() ? OperationStatus.Status.SUCCESS : OperationStatus.Status.FAILURE;
        try {
            repository.save(new OperationStatus(null, userId, OperationStatus.Action.CREATE, status, new Date(), LAYER_ID, generateMessage() ));
        } catch (JsonProcessingException e) {
            logger.warn("Unable to save report. Cause: " + e.getMessage());
        }
    }

    private String generateMessage() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new Message(successions.get(), fails.get()));
    }


}
