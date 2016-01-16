package org.w2fc.geoportal.ws.geometry;

import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.Date;
import java.util.HashSet;

public interface GeometryParameter {

    String getName();

    HashSet<GeoObjectTag> getTags();

    Date getTimetick();

    Long getLayerId();
}
