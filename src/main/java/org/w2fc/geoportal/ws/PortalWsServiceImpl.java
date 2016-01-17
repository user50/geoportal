package org.w2fc.geoportal.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.Constants;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.layer.GeoLayerUI;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.model.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;


@Service
public class PortalWsServiceImpl implements PortalWsService {
    
    @Autowired
    private ServiceRegistry serviceRegistry;

	@Autowired
	private GeoObjectsService geoObjectsService;

    final Logger logger = LoggerFactory.getLogger(PortalWsServiceImpl.class);

    @Override
    @PreAuthorize("@geoportalSecurity.isLayerReader(#layerId)")
    @Transactional(readOnly=true)
    public GetObjectsResponse getObjects(Long layerId){
        
        
        List<GeoObject> objectByTags = serviceRegistry.getGeoObjectDao().listByLayerIdWithTags(layerId);
        
        GetObjectsResponse response = new GetObjectsResponse();
        response.setList(ObjectFactory.createGeoObjectFullAdapterList(objectByTags));
        
        return response;
    }
    
	@Override
	@PreAuthorize("@geoportalSecurity.isLayerReader(#layerId)")
    @Transactional(readOnly=true)
	public GetObjectsResponse getObjectsPartial(Long layerId, Long start,
			Long count) {
		
		 List<GeoObject> objectByTags = serviceRegistry.getGeoObjectDao().listByLayerIdPartial(layerId, start, count);
	        
	     GetObjectsResponse response = new GetObjectsResponse();
	     response.setList(ObjectFactory.createGeoObjectFullAdapterList(objectByTags));
	      
	     return response;
	}
    
	
	@Override
	@PreAuthorize("@geoportalSecurity.isLayerReader(#layerId)")
    @Transactional(readOnly=true)
	public GetObjectsResponse getObjectsChanged(Long layerId, Long revId) {
		 List<GeoObject> objectByTags = serviceRegistry.getGeoObjectDao().listByLayerIdFromRevision(layerId, revId);
	     GetObjectsResponse response = new GetObjectsResponse();
	     response.setList(ObjectFactory.createGeoObjectFullAdapterList(objectByTags));
	      
	     return response;
	}
    
    @Override
    @Transactional(readOnly=true)
    public GetLayersResponse getLayers() {
        List<GeoLayerUIAdapter> l = null;
        if(Constants.isAdministrator()){
            l = ObjectFactory
                    .createGeoLayerUIAdapterList(
                    		serviceRegistry.getLayerDao().list());
        }else{
        
         l = ObjectFactory
                 .createGeoLayerUIAdapterList(serviceRegistry.getLayerDao().listByUserId(
                                serviceRegistry.getUserDao().getCurrentGeoUser().getId()));
        }
        GetLayersResponse resp = new GetLayersResponse();
        resp.setList( new ArrayList<GeoLayerUI>(l));
        
        return resp;
    }

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerReader(#layerId)")
    @Transactional(readOnly=true)
	public Long getObjectsCount(Long layerId) {
		return serviceRegistry.getLayerDao().getObjectsCount(layerId);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerReader(#layerId)")
	@Transactional(readOnly=true)
	public Boolean getHasChanged(Long layerId, Long revId) {
		Long lastRevision =  serviceRegistry.getLayerDao().getObjectsLastRevision(layerId);
		Long lastDeleteRevision = serviceRegistry.getLayerDao().getObjectsLastDeleteRevision();		
		return lastRevision > revId || lastDeleteRevision > revId;
	}

	@Override
	@Transactional(readOnly=true)
	public GetObjectsResponse getPin(Double lat, Double lon) {
		List<GeoLayer> layerList = null;
		if (Constants.isAdministrator()) {
			layerList = serviceRegistry.getLayerDao().list();
		} else {
			layerList = serviceRegistry.getLayerDao().listTreeLayers(serviceRegistry.getUserDao().getCurrentGeoUser().getId());
		}
		if (layerList != null && layerList.size() > 0) {
			List<Long> layerIdList = new ArrayList<Long>();
			for(GeoLayer layer : layerList){
				layerIdList.add(layer.getId());
			}
			List<GeoObject> objs = serviceRegistry.getGeoObjectDao().getByPointAndLayers(lat, lon, layerIdList);
			GetObjectsResponse response = new GetObjectsResponse();
			response.setList(ObjectFactory.createGeoObjectFullAdapterList(objs));
		     
		    return response;
		} else {
			return null;
		}
	}

	@Override
	public Long getLastRevision(Long layerId) {
		Long lastRevision =  serviceRegistry.getLayerDao().getObjectsLastRevision(layerId);
		Long lastDeleteRevision = serviceRegistry.getLayerDao().getObjectsLastDeleteRevision();
		return Math.max(lastRevision, lastDeleteRevision);
	}

	@Override
	@Transactional(readOnly=true)
	//@PreAuthorize("@geoportalSecurity.isObjectAllowed(#id)")
	public GeoObjectFullAdapter getObject(Long id) {
		return new GeoObjectFullAdapter(serviceRegistry.getGeoObjectDao().get(id));
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#rp.layerId)")
	@Transactional
	public Long createPoint(RequestPoint rp) {
		return geoObjectsService.createPoint(rp);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#requestLine.layerId)")
	@Transactional
	public Long createLine(RequestLine requestLine) {
		return geoObjectsService.createLine(requestLine);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#requestLine.layerId)")
	@Transactional
	public Long createPolygon(RequestPolygon requestLine) {
		return geoObjectsService.createPolygon(requestLine);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#request.layerId)")
	@Transactional
	public void updatePoint(Long id, RequestPoint request) {
		geoObjectsService.updatePoint(id, request);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#request.layerId)")
	@Transactional
	public void updateLine(Long id, RequestLine request) {
		geoObjectsService.updateLine(id, request);
	}

	@Override
	@PreAuthorize("@geoportalSecurity.isLayerEditor(#request.layerId)")
	@Transactional
	public void updatePolygon(Long id, RequestPolygon request) {
		geoObjectsService.updatePolygon(id, request);
	}

	@Override
	@Transactional
	public void delete(Long layerId, Long id) {
		geoObjectsService.delete(layerId, id);
	}

}
