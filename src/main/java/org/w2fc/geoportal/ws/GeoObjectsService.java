package org.w2fc.geoportal.ws;

import com.vividsolutions.jts.geom.*;
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
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.geometry.*;
import org.w2fc.geoportal.ws.model.DateAdapter;
import org.w2fc.geoportal.ws.model.RequestLine;
import org.w2fc.geoportal.ws.model.RequestPoint;
import org.w2fc.geoportal.ws.model.RequestPolygon;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Service
public class GeoObjectsService {

    final Logger logger = LoggerFactory.getLogger(GeoObjectsService.class);

    private ServiceRegistry serviceRegistry;

    @Autowired
    public GeoObjectsService(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public Long createPoint(RequestPoint rp)
    {
        return createGeoObject(rp, new PointGeometryBuilder());
    }

    public Long createLine(RequestLine rp)
    {
        return createGeoObject(rp, new LineGeometryBuilder());
    }

    public Long createPolygon(RequestPolygon rp)
    {
        return createGeoObject(rp, new PolygonGeometryBuilder());
    }

    private <T extends GeometryParameter > Long createGeoObject(T parameters, GeometryBuilder<T> geometryBuilder) {
        GeoLayer layer = serviceRegistry.getLayerDao().get(parameters.getLayerId());

        Geometry p = geometryBuilder.create(parameters);

        GeoObject gisObject = ObjectFactory.createGeoObject(parameters.getName(), p);

        checkArea(gisObject);

        if(parameters.getTags() != null && parameters.getTags().size() > 0){
            for(GeoObjectTag tag : parameters.getTags()){
                tag.setGeoObject(gisObject);
            }
            gisObject.setTags(parameters.getTags());
        }
        if(parameters.getTimetick() != null){
            try {
                ObjectFactory.createGeoObjectTag(gisObject, "timetick", new DateAdapter().marshal(parameters.getTimetick()));
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }

        Set<GeoLayer> geoLayers = new HashSet<GeoLayer>();
        geoLayers.add(layer);

        gisObject.setGeoLayers(geoLayers);
        layer.getGeoObjects().add(gisObject);
        gisObject.setCreatedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
        gisObject.setCreated(Calendar.getInstance().getTime());
        GeoObject res = serviceRegistry.getGeoObjectDao().add(gisObject);
        return res.getId();
    }

    public void delete(Long layerId, Long id) {
        serviceRegistry.getGeoObjectDao().remove(id);
    }

    private void checkArea(GeoObject gisObject) {
        Geometry permArea = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object user = auth.getPrincipal();
        if(user instanceof CustomUserDetails){
            if(((CustomUserDetails)user).getPermissionArea() == null)return;
            permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
            if(permArea == null)return;
            if(!permArea.contains(gisObject.getTheGeom()))throw new RuntimeException("������� �������������� ����������");
        }
    }
}