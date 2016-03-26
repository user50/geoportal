package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.layer.GeoLayerUIAdapter;

import java.util.Comparator;

/**
 * @author Yevhen
 */
public class GeoLayersByParentComparator implements Comparator<GeoLayerUIAdapter> {
    @Override
    public int compare(GeoLayerUIAdapter o1, GeoLayerUIAdapter o2) {
        if (o2.getGeoLayer().getParentId() != null && o2.getGeoLayer().getParentId().equals(o1.getGeoLayer().getId()) )
            return -1;

        if (o1.getGeoLayer().getParentId() != null && o1.getGeoLayer().getParentId().equals(o2.getGeoLayer().getId()))
            return 1;

        return 0;
    }
}
