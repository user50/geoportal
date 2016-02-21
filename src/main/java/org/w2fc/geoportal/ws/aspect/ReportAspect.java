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

    // create actions

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public Long aroundCreateSoap(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        try{
            Long res = (Long) joinPoint.proceed();

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

            repository.save(actionStatus);

            return res;
        } catch (Exception e){

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), e.getMessage());

            repository.save(actionStatus);
            return null;
        }
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.GeometryParameter)))")
    public Long aroundCreateRest(ProceedingJoinPoint joinPoint) throws Throwable {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        try{
            Long res = (Long) joinPoint.proceed();

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.CREATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

            repository.save(actionStatus);

            return res;
        } catch (Exception e){

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), e.getMessage());

            repository.save(actionStatus);
            return null;
        }
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))")
    public void aroundUpdateSoap(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

            repository.save(actionStatus);

        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), e.getMessage());

            repository.save(actionStatus);
        }
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.updateObject(Long, org.w2fc.geoportal.ws.model.GeometryParameter)))")
    public void aroundUpdateRest(ProceedingJoinPoint joinPoint) throws Throwable {
        GeometryParameter requestGeoObject = (GeometryParameter) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();

            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.UPDATE, OperationStatus.Status.SUCCESS, new Date(), requestGeoObject.getLayerId());

            repository.save(actionStatus);

        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(requestGeoObject.getGuid(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.UPDATE, OperationStatus.Status.FAILURE, new Date(), requestGeoObject.getLayerId(), e.getMessage());

            repository.save(actionStatus);
        }
    }

    @Around("execution(* org.w2fc.geoportal.ws.GeoObjectService.delete(..)))")
    public void aroundDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Long guid = (Long) joinPoint.getArgs()[0];

        try{
            joinPoint.proceed();


            Long layerId = getLayerId(guid);
            OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.DELETE, OperationStatus.Status.SUCCESS, new Date(), layerId);

            repository.save(actionStatus);

        } catch (Exception e){
            OperationStatus actionStatus = new OperationStatus(guid.toString(), getPid(), getCurrentUserId(),
                    OperationStatus.Action.DELETE, OperationStatus.Status.FAILURE, new Date(), null);

            repository.save(actionStatus);
        }
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
