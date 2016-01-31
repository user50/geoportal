package org.w2fc.geoportal.ws.geometry.builder;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.RequestLine;

public class LineGeometryBuilder implements GeometryBuilder<RequestLine> {

    @Override
    public LineString create(RequestLine parameters) {
        if (parameters.getPointsCoordinates().length < 2)
            throw new InvalidGeometryException("At least two points must be specified");

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        int countOfPoints = parameters.getPointsCoordinates().length;
        Coordinate[] coordinates = new Coordinate[countOfPoints];

        for (int i = 0; i < countOfPoints; i++) {
            coordinates[i] = new Coordinate(parameters.getPointsCoordinates()[i].getLon(), parameters.getPointsCoordinates()[i].getLat());
        }

        return factory.createLineString(coordinates);
    }
}
