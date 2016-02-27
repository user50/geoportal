package org.w2fc.geoportal.ws.http;

import com.drew.lang.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectProperties;
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;

@Service
public class GeoObjectDrawPropertiesService {

    private GeoObjectDao geoObjectDao;

    @Autowired
    public GeoObjectDrawPropertiesService(GeoObjectDao geoObjectDao) {
        this.geoObjectDao = geoObjectDao;
    }

    public void save(Long id, GeoObjectDrawProperties properties){
        GeoObject geoObject = geoObjectDao.get(id);

        if (geoObject == null)
            throw new GeoObjectNotFoundException("Geo object with id: " +  id + " is not found!!!!!!!!!!!");

        GeoObjectProperties props = geoObject.getGeoObjectProperties();
        if(props == null){
            props = ObjectFactory.createGeoObjectProperties(geoObject);
        }
        props.setFillColor(properties.getFillColor());
        props.setLineColor(properties.getLineColor());
        props.setLineWeight(properties.getLineWeight());

        geoObjectDao.update(geoObject);
    }
}
