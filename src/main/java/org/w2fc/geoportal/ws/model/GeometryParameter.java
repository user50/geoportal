package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.model.GeoObjectGeometryType;

import java.util.Set;

public interface GeometryParameter {

    GeoObjectGeometryType getType();

    String getName();

    Set<GeoObjectTag> getTags();

    Set<Long> getLayerIds();

    String getWkt();

    String getSpatialKey();

    String getGuid();

    String getExtSysId();
}
