package org.w2fc.geoportal.ws.geometry;

import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.Date;
import java.util.Set;

public interface GeometryParameter {

    String getName();

    Set<GeoObjectTag> getTags();

    Date getTimetick();

    Long getLayerId();

    String getWkt();
}
