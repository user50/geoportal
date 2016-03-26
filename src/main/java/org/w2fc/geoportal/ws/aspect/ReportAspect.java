package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.config.ThreadPids;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yevhen
 */
@Aspect
public class ReportAspect {

    final Logger logger = LoggerFactory.getLogger(ReportAspect.class);

    private static final Long LAYER_ID = 1L; // todo getting layer

    private OperationStatusRepository repository;
    private GeoUserDao geoUserDao;
    private GeoObjectDao geoObjectDao;

    public ReportAspect(OperationStatusRepository repository, GeoUserDao geoUserDao, GeoObjectDao geoObjectDao) {
        this.repository = repository;
        this.geoUserDao = geoUserDao;
        this.geoObjectDao = geoObjectDao;
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning= "result")
    public void aroundCreateSoapSuccess(JoinPoint joinPoint, Object result) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().toString());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundCreateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))",
            returning= "result")
    public void aroundCreateRestSuccess(JoinPoint joinPoint, Object result) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().toString());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundCreateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning= "result")
    public void aroundUpdateSoapSuccess(JoinPoint joinPoint, Object result) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().toString());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundUpdateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))",
            returning= "result")
    public void aroundUpdateRestSuccess(JoinPoint joinPoint, Object result) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().toString());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundUpdateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(Long)))",
            returning= "result")
    public void afterDeleteRestSuccess(JoinPoint joinPoint, Object result) {
        Long id = (Long) joinPoint.getArgs()[0];
        Set<Long> layerIds = getLayerIds(id);

        OperationStatus actionStatus = new OperationStatus(id.toString(), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerIds.toString());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(Long)))",
            throwing= "error")
    public void afterDeleteRestFail(JoinPoint joinPoint, Throwable error) {
        Long id = (Long) joinPoint.getArgs()[0];
        Set<Long> layerIds = getLayerIds(id);

        OperationStatus actionStatus = new OperationStatus(getGuid(id), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerIds.toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(String, String)))",
            returning= "result")
    public void afterDeleteSoapSuccess(JoinPoint joinPoint, Object result) {
        String id = (String) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(id, getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), null);

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(String, String)))",
            throwing= "error")
    public void afterDeleteSoapFail(JoinPoint joinPoint, Throwable error) {
        String extSysId = (String) joinPoint.getArgs()[0];
        String guid = (String) joinPoint.getArgs()[1];

        if (error instanceof GeoObjectNotFoundException) {
            OperationStatus actionStatus = new OperationStatus(guid, getPid(), getCurrentUserId(),
                    OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), null, getErrorMessage(error));
            repository.save(actionStatus);
            return;
        }

        Long id = geoObjectDao.getGeoObjectId(guid, extSysId);

        Set<Long> layerIds = getLayerIds(id);

        OperationStatus actionStatus = new OperationStatus(guid, getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerIds.toString(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    public Long getCurrentUserId() {
        return geoUserDao.getCurrentGeoUser().getId();
    }

    private Set<Long> getLayerIds(Long objectId)
    {
        GeoObject geoObject = geoObjectDao.get(objectId);

        if (geoObject == null)
            return null;

        Set<Long> layerIds = new HashSet<Long>();
        for (GeoLayer geoLayer : geoObject.getGeoLayers()) {
            layerIds.add(geoLayer.getId());
        }
        return layerIds;
    }

    private String getPid(){
        return ThreadPids.INSTANCE.getPid(Thread.currentThread().getId());
    }

    private String getErrorMessage(Throwable error) {
        return error.getClass() + ": " + error.getMessage();
    }

    private String getGuid(Long id) {
        GeoObject geoObject = geoObjectDao.get(id);

        if (geoObject == null)
            return null;

        return geoObject.getGuid();
    }

}
