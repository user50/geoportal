package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.config.ThreadPids;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

import java.util.Date;

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

    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundCreateSoapSuccess(JoinPoint joinPoint) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundCreateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), error.getMessage());

        repository.save(actionStatus);
    }

    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))")
    public void aroundCreateRestSuccess(JoinPoint joinPoint) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundCreateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), error.getMessage());

        repository.save(actionStatus);
    }

    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundUpdateSoapSuccess(JoinPoint joinPoint) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundUpdateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), error.getMessage());

        repository.save(actionStatus);
    }

    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))")
    public void aroundUpdateRestSuccess(JoinPoint joinPoint) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundUpdateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), error.getMessage());

        repository.save(actionStatus);
    }

    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))")
    public void afterDeleteSuccess(JoinPoint joinPoint) {
        Long guid = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(guid);

        OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerId);

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))",
            throwing= "error")
    public void afterDeleteFail(JoinPoint joinPoint, Throwable error) {
        Long guid = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(guid);

        OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerId, error.getMessage());

        repository.save(actionStatus);
    }


    public Long getCurrentUserId() {
        return geoUserDao.getCurrentGeoUser().getId();
    }

    private Long getLayerId(Long objectId)
    {
        GeoObject geoObject = geoObjectDao.get(objectId);

        if (geoObject == null)
            return null;

        return geoObject.getGeoLayers().iterator().next().getId();
    }

    private String getPid(){
        return ThreadPids.INSTANCE.getPid(Thread.currentThread().getId());
    }
}
