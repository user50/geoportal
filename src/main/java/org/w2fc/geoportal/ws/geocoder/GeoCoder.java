package org.w2fc.geoportal.ws.geocoder;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Yevhen
 */
public interface GeoCoder {
    public Coordinate getCoordinatesByAddress(String address);
}
