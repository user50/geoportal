package org.w2fc.geoportal.ws;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.WebServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.w2fc.geoportal.ws.async.SOAPProcessStatus;
import org.w2fc.geoportal.ws.error.ErrorsReport;
import org.w2fc.geoportal.ws.model.DeleteObjectsRequest;
import org.w2fc.geoportal.ws.model.DeleteSoapResponse;
import org.w2fc.geoportal.ws.model.GeoObjectFullAdapter;
import org.w2fc.geoportal.ws.model.GetLayersResponse;
import org.w2fc.geoportal.ws.model.GetObjectsResponse;
import org.w2fc.geoportal.ws.model.RequestGeoObject;
import org.w2fc.geoportal.ws.model.SOAPOperationResponse;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@WebService(serviceName="PortalWs")
public class PortalWs extends SpringBeanAutowiringSupport {
    
    @Autowired
    private PortalWsService portalWsService;

    @Resource
    private WebServiceContext webServiceContext;

    private ObjectMapper objectMapper = new ObjectMapper();

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
    public SOAPOperationResponse createObjects(List<RequestGeoObject> rp){
        autowire();
        
        String pid = portalWsService.createObjects(rp);

        return new SOAPOperationResponse(pid);
    }

    @WebMethod
    public SOAPOperationResponse updateObjects(List<RequestGeoObject> rp){
        autowire();
        String pid = portalWsService.updateObjects(rp);

        return new SOAPOperationResponse(pid);
    }

    @WebMethod
    public SOAPOperationResponse deleteObjects(DeleteObjectsRequest deleteObjectsRequest){
        autowire();

        String[] idList;
        try {
            idList = objectMapper.readValue(deleteObjectsRequest.getIds(), String[].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse json array");
        }
        String pid = portalWsService.deleteObjects(deleteObjectsRequest.getExtSysId(), Arrays.asList(idList));

        return new SOAPOperationResponse(pid);
    }

    @WebMethod
    public DeleteSoapResponse getStatus(@XmlElement(required=true, name="pid") String pid){
        autowire();
        Object statusObj = SOAPProcessStatus.INSTANCE.get(pid);

        if (statusObj instanceof String)
            return new DeleteSoapResponse((String)statusObj);
        else {
            ErrorsReport errorsReport = (ErrorsReport) statusObj;
            return new DeleteSoapResponse("error", errorsReport.getErrors());
        }
    }

    @WebMethod
    public List<String> getSpatialRefSystems(){
        autowire();
        return portalWsService.getSpatialRefSystems();
    }

    private void  autowire(){
        if(null == portalWsService){
            processInjectionBasedOnCurrentContext(this);
        }
    }
}
