package org.w2fc.geoportal.ws;

import com.vividsolutions.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.ReferenceSystemProj;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;
import org.w2fc.geoportal.ws.geometry.builder.GeometryBuilder;
import org.w2fc.geoportal.ws.geometry.builder.GeometryBuilderFactory;
import org.w2fc.geoportal.ws.geometry.factory.ByTypeGeometryParameterFactory;
import org.w2fc.geoportal.ws.model.GeometryParameter;
import org.w2fc.geoportal.ws.model.RequestGeoObject;

import java.util.*;

/**
 * @author Yevhen
 */
@Service
public class GeoObjectService {

    final Logger logger = LoggerFactory.getLogger(GeoObjectService.class);

    private ServiceRegistry serviceRegistry;

    @Autowired
    public GeoObjectService(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public GeoObjectService() {
    }


    public Long createAndSaveObject(RequestGeoObject requestGeoObject){
        GeometryParameter geometryParameter = new ByTypeGeometryParameterFactory(requestGeoObject).create();
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);

        GeoObject geoObject = createGeoObject(geometryParameter, geometryBuilder);

        return save(geoObject);
    }

    public Long createAndSaveObject(GeometryParameter geometryParameter){
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        GeoObject geoObject = createGeoObject(geometryParameter, geometryBuilder);

        return save(geoObject);
    }

    public void updateObject(RequestGeoObject requestGeoObject) {
        checkExists(requestGeoObject.getId());

        GeometryParameter geometryParameter = new ByTypeGeometryParameterFactory(requestGeoObject).create();
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        updateGeoObject(requestGeoObject.getId(), geometryParameter, geometryBuilder);
    }

    public void updateObject(Long id, GeometryParameter geometryParameter){
        checkExists(id);
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        updateGeoObject(id, geometryParameter, geometryBuilder);
    }

    public void delete(Long id) {
        checkExists(id);
        serviceRegistry.getGeoObjectDao().remove(id);
    }


    private <T extends GeometryParameter > GeoObject createGeoObject(T params, GeometryBuilder<T> geometryBuilder) {
        GeoLayer layer = serviceRegistry.getLayerDao().get(params.getLayerId());

        Geometry geometry = geometryBuilder.create(params);

        GeoObject gisObject = ObjectFactory.createGeoObject(params.getName(), geometry);

        checkArea(gisObject);

        if(params.getTags() != null && params.getTags().size() > 0){
            for(GeoObjectTag tag : params.getTags()){
                tag.setGeoObject(gisObject);
            }
            gisObject.setTags(params.getTags());
        }

        Set<GeoLayer> geoLayers = new HashSet<GeoLayer>();
        geoLayers.add(layer);

        gisObject.setGeoLayers(geoLayers);
        layer.getGeoObjects().add(gisObject);
        gisObject.setCreatedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
        gisObject.setCreated(Calendar.getInstance().getTime());

        return gisObject;
    }

    private Long save(GeoObject geoObject) {
        GeoObject res = serviceRegistry.getGeoObjectDao().add(geoObject);
        return res.getId();
    }

    public <T extends GeometryParameter> void updateGeoObject(long id, T params, GeometryBuilder<T> geometryBuilder)
    {
        GeoObject gisObject = serviceRegistry.getGeoObjectDao().get(id);

        if (gisObject == null)
            throw new IllegalArgumentException("Geo object with id "+id+" doesn't exist");

        gisObject.setName(params.getName());

        gisObject.setTheGeom(geometryBuilder.create(params));

        serviceRegistry.getGeoObjectDao().update(gisObject);
    }

    private void checkArea(GeoObject gisObject) {
        Geometry permArea = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object user = auth.getPrincipal();
        if(user instanceof CustomUserDetails){
            if(((CustomUserDetails)user).getPermissionArea() == null)return;
            permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
            if(permArea == null)return;
            if(!permArea.contains(gisObject.getTheGeom()))throw new RuntimeException("The area is not available for editing");
        }
    }

    private void checkExists(Long id){
        GeoObject geoObject = serviceRegistry.getGeoObjectDao().get(id);
        if (geoObject == null)
            throw new GeoObjectNotFoundException("Geo object with id #" + id + " does not exist");
    }

    public List<String> getSpatialRefSystems(){
        List<ReferenceSystemProj> list = serviceRegistry.getReferenceSystemProjDao().list();
        List<String> refKeys = new ArrayList<String>();
        for (ReferenceSystemProj systemProj : list) {
            refKeys.add(systemProj.getKey());
        }

        return refKeys;
    }
    
}
