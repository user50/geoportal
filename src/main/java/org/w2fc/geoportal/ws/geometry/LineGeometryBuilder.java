package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.RequestLine;

public class LineGeometryBuilder implements GeometryBuilder<RequestLine> {

    @Override
    public LineString create(RequestLine parameters) {
        if (parameters.getLats().length != parameters.getLons().length)
            throw new InvalidGeometryException("Counts of latitudes and longitudes must be equal");

        if (parameters.getLats().length < 2 || parameters.getLons().length < 2)
            throw new InvalidGeometryException("At least coordinates for two points must be specified");

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        int countOfPoints = parameters.getLats().length;
        Coordinate[] coordinates = new Coordinate[countOfPoints];

        for (int i = 0; i < countOfPoints; i++) {
            coordinates[i] = new Coordinate(parameters.getLons()[i], parameters.getLats()[i]);
        }

        return factory.createLineString(coordinates);
    }
}
