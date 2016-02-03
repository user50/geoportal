package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

@Aspect
public class CreateObjectsAspect {

    CreateObjectReportService reportService;

    public CreateObjectsAspect(CreateObjectReportService reportService) {
        this.reportService = reportService;
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject)))",
            returning = "id")
    public void afterCreateObjectSuccess(JoinPoint joinPoint, Long id) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        reportService.collectSuccessCreatedObject(requestGeoObject.getGuid());
    }

    @AfterThrowing(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectService.createAndSaveObject(org.w2fc.geoportal.ws.model.RequestGeoObject))",
            throwing= "error")
    public void afterCreateObjectFail(JoinPoint joinPoint, Throwable error) {
        RequestGeoObject requestGeoObject = (RequestGeoObject) joinPoint.getArgs()[0];

        reportService.collectCreationFailedObject(requestGeoObject.getGuid(), error.getMessage());
    }

    @AfterReturning(
            pointcut = "execution(* org.w2fc.geoportal.ws.GeoObjectsService.createObjects(..)))",
            returning = "id")
    public void afterCreateObjectsSuccess(JoinPoint joinPoint, Long id) {
        reportService.saveReport();
    }




}
