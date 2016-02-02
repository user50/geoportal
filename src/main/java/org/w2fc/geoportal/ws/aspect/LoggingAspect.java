package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

/**
 * @author Yevhen
 */
@Aspect
public class LoggingAspect {

    final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private OperationStatusRepository repository;

    public LoggingAspect(OperationStatusRepository repository) {
        this.repository = repository;
    }

    // create actions
    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(..)))",
            returning = "id")
    public void afterCreateSuccess(JoinPoint joinPoint, Long id) {
        OperationStatus actionStatus = new OperationStatus(id, OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS);
        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(..))",
            throwing= "error")
    public void afterCreateFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE);
        actionStatus.setMessage(error.getMessage());

        repository.save(actionStatus);
    }

    // update actions
    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(..)))",
            returning = "id")
    public void afterUpdateSuccess(JoinPoint joinPoint, Long id) {
        OperationStatus actionStatus = new OperationStatus(id, OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS);
        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(..))",
            throwing= "error")
    public void afterUpdateFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE);
        actionStatus.setMessage(error.getMessage());

        repository.save(actionStatus);
    }

    //delete actions
    @After("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..))")
    public void afterDeleteSuccess(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(id, OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS);
        repository.save(actionStatus);
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..))",
            throwing= "error")
    public void afterDeleteFail(JoinPoint joinPoint, Throwable error) {
        Long id = (Long) joinPoint.getArgs()[0];
        OperationStatus actionStatus = new OperationStatus(id, OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE);
        actionStatus.setMessage(error.getMessage());
        repository.save(actionStatus);
    }
}
