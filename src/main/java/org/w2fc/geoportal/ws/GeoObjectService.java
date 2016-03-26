package org.w2fc.geoportal.ws;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.auth.GeoportalSecurity;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.ReferenceSystemProj;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;
import org.w2fc.geoportal.ws.exception.LayerAccessDeniedException;
import org.w2fc.geoportal.ws.exception.MissingParameterException;
import org.w2fc.geoportal.ws.exception.NonUniqueIdentifierException;
import org.w2fc.geoportal.ws.geometry.builder.GeometryBuilder;
import org.w2fc.geoportal.ws.geometry.builder.GeometryBuilderFactory;
import org.w2fc.geoportal.ws.geometry.builder.TransformCoordinate;
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
    private GeoportalSecurity geoportalSecurity;

    @Autowired
    public GeoObjectService(ServiceRegistry serviceRegistry, GeoportalSecurity geoportalSecurity) {
        this.serviceRegistry = serviceRegistry;
        this.geoportalSecurity = geoportalSecurity;
    }

    public GeoObjectService() {
    }


    public Long createAndSaveObject(RequestGeoObject requestGeoObject){
        new CreateGeoObjectValidator().validateCreate(requestGeoObject);

        if (geoportalSecurity.checkExists(requestGeoObject.getExtSysId(), requestGeoObject.getGuid()))
            throw new NonUniqueIdentifierException("Non unique identifier: guid - " + requestGeoObject.getGuid() );

        GeometryParameter geometryParameter = new ByTypeGeometryParameterFactory(requestGeoObject).create();
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);

        GeoObject geoObject = createGeoObject(geometryParameter, geometryBuilder);

        return save(geoObject);
    }

    public Long createAndSaveObject(GeometryParameter geometryParameter){
        new CreateGeoObjectValidator().validateCreate(geometryParameter);

        if (geoportalSecurity.checkExists(geometryParameter.getExtSysId(), geometryParameter.getGuid()))
            throw new NonUniqueIdentifierException("Non unique identifier: guid - " + geometryParameter.getGuid() );

        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        GeoObject geoObject = createGeoObject(geometryParameter, geometryBuilder);

        return save(geoObject);
    }

    public void updateObject(RequestGeoObject requestGeoObject) {
        new CreateGeoObjectValidator().validateUpdate(requestGeoObject);

        Long id = serviceRegistry.getGeoObjectDao().getGeoObjectId(requestGeoObject.getGuid(), requestGeoObject.getExtSysId());

        if (id == null)
            throw new GeoObjectNotFoundException("Geo object does not exist");

        GeometryParameter geometryParameter = new ByTypeGeometryParameterFactory(requestGeoObject).create();
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        updateGeoObject(id, geometryParameter, geometryBuilder);
    }

    public void updateObject(Long id, GeometryParameter geometryParameter){
        geoportalSecurity.checkExists(id);
        GeometryBuilder geometryBuilder = new GeometryBuilderFactory(serviceRegistry.getGeoCoder(), serviceRegistry.getReferenceSystemProjDao()).create(geometryParameter);
        updateGeoObject(id, geometryParameter, geometryBuilder);
    }

    public void delete(String extSysId, String guid) {
        Long id = serviceRegistry.getGeoObjectDao().getGeoObjectId(guid, extSysId);

        delete(id);
    }

    public void delete(Long id) {
        geoportalSecurity.checkExists(id);

        if (!geoportalSecurity.checkLayerPermissions(id))
            throw new LayerAccessDeniedException("Can not delete the geoobject. Access denied to the layer.");

        GeoObject geoObject = serviceRegistry.getGeoObjectDao().get(id);
        geoportalSecurity.checkArea(geoObject);

        serviceRegistry.getGeoObjectDao().remove(id);
    }


    private <T extends GeometryParameter > GeoObject createGeoObject(T params, GeometryBuilder<T> geometryBuilder) {

        Set<GeoLayer> layers = serviceRegistry.getLayerDao().list(params.getLayerIds());

        Geometry geometry = geometryBuilder.create(params);

        GeoObject gisObject = ObjectFactory.createGeoObject(params.getName(), geometry);

        geoportalSecurity.checkArea(gisObject);

        if(params.getTags() != null && params.getTags().size() > 0){
            for(GeoObjectTag tag : params.getTags()){
                tag.setGeoObject(gisObject);
            }
            gisObject.setTags(params.getTags());
        }

        Set<GeoLayer> geoLayers = new HashSet<GeoLayer>();
        geoLayers.addAll(layers);

        gisObject.setGeoLayers(geoLayers);
        gisObject.setCreatedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
        gisObject.setCreated(Calendar.getInstance().getTime());
        gisObject.setExtSysId(params.getExtSysId());
        gisObject.setGuid(params.getGuid());

        return gisObject;
    }

    private Long save(GeoObject geoObject) {
        GeoObject res = serviceRegistry.getGeoObjectDao().add(geoObject);
        Long id = res.getId();
        Iterator<GeoLayer> iterator = geoObject.getGeoLayers().iterator();
        while (iterator.hasNext()){
            GeoLayer layer = iterator.next();
            try {
                serviceRegistry.getGeoObjectDao().addToLayer(id, layer.getId());
            } catch (Exception e) {
                throw new RuntimeException("Unable  to add to layer ");
            }
        }
        return id;
    }

    public <T extends GeometryParameter> void updateGeoObject(long id, T params, GeometryBuilder<T> geometryBuilder)
    {
        GeoObject gisObject = serviceRegistry.getGeoObjectDao().getWithTags(id);

        if (gisObject == null)
            throw new IllegalArgumentException("Geo object with id "+id+" doesn't exist");

        updateObjectLayer(gisObject, params.getLayerIds());

        gisObject.setName(params.getName());
        new CreateOrUpdateGeoTag().createUpdate(gisObject, params.getTags());
        gisObject.setTheGeom(geometryBuilder.create(params));

        geoportalSecurity.checkArea(gisObject);

        serviceRegistry.getGeoObjectDao().mergeUpdate(gisObject);
    }

    private void updateObjectLayer(GeoObject gisObject, Set<Long> layerIds) {
        if (layerIds == null)
            return;

        for (GeoLayer currentLayer : gisObject.getGeoLayers()) {
            serviceRegistry.getGeoObjectDao().deleteObjectListFromLayer(currentLayer.getId(), Arrays.asList(gisObject.getId()));
        }

        for (Long layerId : layerIds) {
            GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
            if (layer == null)
                throw new MissingParameterException("Geo layer with id " + layerId +" does not exist");
            try {
                serviceRegistry.getGeoObjectDao().addToLayer(gisObject.getId(), layer.getId());
            } catch (Exception e) {
                throw new RuntimeException("Unable  to add to layer " + layerId);
            }
        }
    }

    public List<String> getSpatialRefSystems(){
        List<ReferenceSystemProj> list = serviceRegistry.getReferenceSystemProjDao().list();
        List<String> refKeys = new ArrayList<String>();
        refKeys.add(TransformCoordinate.DEFAULT_SPATIAL_REF_SYS);//add default spatial ref sys to the list

        for (ReferenceSystemProj systemProj : list) {
            refKeys.add(systemProj.getKey());
        }

        return refKeys;
    }
    
}
