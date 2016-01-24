package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.RequestPolygon;

public class PolygonGeometryBuilder implements GeometryBuilder<RequestPolygon> {

    @Override
    public Geometry create(RequestPolygon parameters) {
        if (parameters.getLats().length != parameters.getLons().length)
            throw new InvalidGeometryException("Counts of latitudes and longitudes must be equal");

        if (parameters.getLats().length < 4 || parameters.getLons().length < 4)
            throw new InvalidGeometryException("At least coordinates for four points must be specified");

        if (!parameters.getLats()[0].equals(parameters.getLats()[parameters.getLats().length-1]) ||
            !parameters.getLons()[0].equals(parameters.getLons()[parameters.getLons().length-1]))
            throw new InvalidGeometryException("Points do not form a closed polygon");

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        Coordinate[] coordinates = new Coordinate[parameters.getLons().length];
        for (int i=0;i<parameters.getLons().length; i++ )
            coordinates[i] = new Coordinate(parameters.getLons()[i], parameters.getLats()[i]);

        LinearRing linearRing = factory.createLinearRing(coordinates);

        return factory.createPolygon(linearRing,null);
    }
}
