package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

import java.util.Date;

/**
 * @author Yevhen
 */
@Aspect
public class ReportAspect {

    final Logger logger = LoggerFactory.getLogger(ReportAspect.class);

    private static final Long LAYER_ID = 1L;

    private OperationStatusRepository repository;
    private GeoUserDao geoUserDao;

    public ReportAspect(OperationStatusRepository repository, GeoUserDao geoUserDao) {
        this.repository = repository;
        this.geoUserDao = geoUserDao;
    }

    // create actions
    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning = "id")
    public void afterCreateSuccess(JoinPoint joinPoint, Long id) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), LAYER_ID);
        actionStatus.setGuid(requestGeoObject.getGuid());
        actionStatus.setiKey(id);

        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject))",
            throwing= "error")
    public void afterCreateFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), LAYER_ID);
        actionStatus.setMessage(error.getMessage());
        actionStatus.setGuid(requestGeoObject.getGuid());

        repository.save(actionStatus);
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();
        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), LAYER_ID);
            actionStatus.setMessage(e.getMessage());
            actionStatus.setGuid(requestGeoObject.getGuid());
            actionStatus.setiKey(requestGeoObject.getId());

            repository.save(actionStatus);
            return;
        }

        OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), LAYER_ID);
        actionStatus.setUserId(getCurrentUserId());
        actionStatus.setGuid(requestGeoObject.getGuid());
        actionStatus.setiKey(requestGeoObject.getId());

        repository.save(actionStatus);
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))")
    public void aroundDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();
        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), LAYER_ID);
            actionStatus.setMessage(e.getMessage());
            actionStatus.setUserId(getCurrentUserId());
            actionStatus.setiKey(id);

            repository.save(actionStatus);
            return;
        }

        OperationStatus actionStatus = new OperationStatus(getCurrentUserId(), OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), LAYER_ID);
        actionStatus.setUserId(getCurrentUserId());
        actionStatus.setiKey(id);

        repository.save(actionStatus);
    }

    public Long getCurrentUserId() {
        return geoUserDao.getCurrentGeoUser().getId();
    }
}
