package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.Set;

public class CreateOrUpdateGeoTag {

    public void createUpdate(GeoObject gisObject, Set<GeoObjectTag> geoObjectTags)
    {
        if(geoObjectTags == null || geoObjectTags.isEmpty() )
            return;


        for (GeoObjectTag geoObjectTag : geoObjectTags) {
            GeoObjectTag existingTag = getTabByKey(gisObject, geoObjectTag.getKey());

            if (existingTag != null) {
                existingTag.setValue(geoObjectTag.getValue());
            }
            else
            {
                geoObjectTag.setGeoObject(gisObject);
                gisObject.addTag(geoObjectTag);
            }
        }
    }

    private GeoObjectTag getTabByKey(GeoObject gisObject, String key)
    {
        for (GeoObjectTag geoObjectTag : gisObject.getTags())
            if(geoObjectTag.getKey().equals(key))
                return geoObjectTag;

        return null;
    }

}
