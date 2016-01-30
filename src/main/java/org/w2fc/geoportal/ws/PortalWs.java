package org.w2fc.geoportal.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.w2fc.geoportal.ws.model.*;

import java.util.Arrays;
import java.util.List;


@Service
@WebService(serviceName="PortalWs")
public class PortalWs extends SpringBeanAutowiringSupport{
    
    @Autowired
    private PortalWsService portalWsService;
    

    @WebMethod
    public String ping(){
        return "passed";
    }
    
    
    @WebMethod
    public GetObjectsResponse getObjects( @XmlElement(required=true, name="layerId") Long layerId){
        autowire();
        return portalWsService.getObjects(layerId);
    }
    
    @WebMethod
    public GetObjectsResponse getObjectsPartial( 
    		@XmlElement(required=true, name="layerId") Long layerId, 
    		@XmlElement(required=true, name="start") Long start,
    		@XmlElement(name="count") Long count
    		){
        autowire();
        return portalWsService.getObjectsPartial(layerId, start, count);
    }
    
    @WebMethod
    public GetObjectsResponse getObjectsChanged( 
    		@XmlElement(required=true, name="layerId") Long layerId, 
    		@XmlElement(required=true, name="revId") Long revId
    		){
        autowire();
        return portalWsService.getObjectsChanged(layerId, revId);
    }
    
    
    @WebMethod
    public GetLayersResponse getLayers(){
        autowire();
        return portalWsService.getLayers();
    }
    
    @WebMethod
    public Long getLayerObjectsCount(@XmlElement(required=true, name="layerId") Long layerId){
        autowire();
        return portalWsService.getObjectsCount(layerId);
    }
    
    @WebMethod
    public Boolean hasChanged(
    		@XmlElement(required=true, name="layerId") Long layerId, 
    		@XmlElement(required=true, name="revId") Long revId
    ){
        autowire();
        return portalWsService.getHasChanged(layerId, revId);
    }
    
    @WebMethod
    public Long getLastRevision(
    		@XmlElement(required=true, name="layerId") Long layerId
    ){
        autowire();
        return portalWsService.getLastRevision(layerId);
    }
    
    @WebMethod
    public GetObjectsResponse getPin( 
    		@XmlElement(required=true, name="lat") Double lat, 
    		@XmlElement(required=true, name="lon") Double lon
    		){
        autowire();
        return portalWsService.getPin(lat, lon);
    }
    
    @WebMethod
    public GeoObjectFullAdapter getObject( 
    		@XmlElement(required=true, name="id") Long id
    		){
        autowire();
        return portalWsService.getObject(id);
    }

    @WebMethod
    public void createObjects(List<RequestGeoObject> rp){
        autowire();
        portalWsService.createObjects(rp);
    }
    
    private void  autowire(){
        if(null == portalWsService){
            processInjectionBasedOnCurrentContext(this);
        }
    }
}
