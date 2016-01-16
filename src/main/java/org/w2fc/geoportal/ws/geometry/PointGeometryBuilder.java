package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.model.RequestPoint;

public class PointGeometryBuilder implements GeometryBuilder<RequestPoint> {
    @Override
    public Geometry create(RequestPoint parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
        Coordinate coordinate = new Coordinate(parameters.getLon(), parameters.getLat());
        return factory.createPoint(coordinate );
    }
}
