package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.w2fc.geoportal.ws.exception.InvalidWKTException;

/**
 * @author yevhenlozov
 */
public class WKTGeometryBuilder implements GeometryBuilder<GeometryParameter> {

    @Override
    public Geometry create(GeometryParameter parameters) {
        WKTReader wktReader = new WKTReader();
        try {
            return wktReader.read(parameters.getWkt());
        } catch (ParseException e) {
            throw new InvalidWKTException("WKT is invalid: " + parameters.getWkt());
        }
    }
}
