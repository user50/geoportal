package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.dao.GeoObjectDao;
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

    private static final Long LAYER_ID = 1L; // todo getting layer

    private OperationStatusRepository repository;
    private GeoUserDao geoUserDao;
    private GeoObjectDao geoObjectDao;

    public ReportAspect(OperationStatusRepository repository, GeoUserDao geoUserDao, GeoObjectDao geoObjectDao) {
        this.repository = repository;
        this.geoUserDao = geoUserDao;
        this.geoObjectDao = geoObjectDao;
    }

    // create actions

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundCreate(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();
        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getId(), getCurrentUserId(),
                    OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId());
            actionStatus.setMessage(e.getMessage());
            actionStatus.setiKey(requestGeoObject.getId());

            repository.save(actionStatus);
            return;
        }

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getId(), getCurrentUserId(),
                OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());
        actionStatus.setUserId(getCurrentUserId());
        actionStatus.setGuid(requestGeoObject.getGuid());

        repository.save(actionStatus);
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();
        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getId(), getCurrentUserId(),
                    OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId());
            actionStatus.setMessage(e.getMessage());
            actionStatus.setiKey(requestGeoObject.getId());

            repository.save(actionStatus);
            return;
        }

        OperationStatus actionStatus = new OperationStatus(requestGeoObject.getId(), getCurrentUserId(),
                OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());
        actionStatus.setUserId(getCurrentUserId());
        actionStatus.setGuid(requestGeoObject.getGuid());

        repository.save(actionStatus);
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))")
    public void aroundDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];
        Long layerId = getLayerId(id);

        try{
            joinPoint.proceed();
        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(id, getCurrentUserId(), OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), layerId);
            actionStatus.setMessage(e.getMessage());
            actionStatus.setUserId(getCurrentUserId());

            repository.save(actionStatus);
            return;
        }

        OperationStatus actionStatus = new OperationStatus(id, getCurrentUserId(), OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerId);
        actionStatus.setUserId(getCurrentUserId());

        repository.save(actionStatus);
    }

    public Long getCurrentUserId() {
        return geoUserDao.getCurrentGeoUser().getId();
    }

    private Long getLayerId(Long objectId)
    {
        return geoObjectDao.get(objectId).getGeoLayers().iterator().next().getId();
    }
}
