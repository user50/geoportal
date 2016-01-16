package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.model.RequestLine;

public class LineGeometryBuilder implements GeometryBuilder<RequestLine> {

    @Override
    public LineString create(RequestLine parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(parameters.getFirstLon(), parameters.getFirstLat());
        coordinates[1] = new Coordinate(parameters.getSecondLon(), parameters.getSecondLat());

        return factory.createLineString(coordinates);
    }
}
