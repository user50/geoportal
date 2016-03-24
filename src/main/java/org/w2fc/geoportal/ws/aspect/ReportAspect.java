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
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;
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

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning= "result")
    public void aroundCreateSoapSuccess(JoinPoint joinPoint, Object result) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().iterator().next());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundCreateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().iterator().next(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))",
            returning= "result")
    public void aroundCreateRestSuccess(JoinPoint joinPoint, Object result) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().iterator().next());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundCreateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().iterator().next(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning= "result")
    public void aroundUpdateSoapSuccess(JoinPoint joinPoint, Object result) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().iterator().next());

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            throwing= "error")
    public void aroundUpdateSoapFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().iterator().next(), getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))",
            returning= "result")
    public void aroundUpdateRestSuccess(JoinPoint joinPoint, Object result) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerIds().iterator().next());

        repository.save(actionStatus);
    }


    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))",
            throwing= "error")
    public void aroundUpdateRestFail(JoinPoint joinPoint, Throwable error) {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerIds().iterator().next(), getErrorMessage(error));

        repository.save(actionStatus);
    }

/*    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))")
    public void aroundDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Long guid = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(guid);

        try{
            joinPoint.proceed();

            OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerId);

            repository.save(actionStatus);

        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerId, getErrorMessage(e));

            repository.save(actionStatus);

            throw e;
        }
    }*/

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(Long)))",
            returning= "result")
    public void afterDeleteRestSuccess(JoinPoint joinPoint, Object result) {
        Long guid = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(guid);

        OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerId);

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(Long)))",
            throwing= "error")
    public void afterDeleteRestFail(JoinPoint joinPoint, Throwable error) {
        Long guid = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(guid);

        OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerId, getErrorMessage(error));

        repository.save(actionStatus);
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(String, String)))",
            returning= "result")
    public void afterDeleteSoapSuccess(JoinPoint joinPoint, Object result) {
        String guid = (String) joinPoint.getArgs()[1];

        OperationStatus actionStatus = new OperationStatus(guid, getPid(), getCurrentUserId(),
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

        Long layerId = getLayerId(id);

        OperationStatus actionStatus = new OperationStatus(guid, getPid(), getCurrentUserId(),
                OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerId, getErrorMessage(error));

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

    private String getErrorMessage(Throwable error) {
        return error.getClass() + ": " + error.getMessage();
    }

}
