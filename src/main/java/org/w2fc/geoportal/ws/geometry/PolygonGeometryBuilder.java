package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.model.RequestPolygon;

public class PolygonGeometryBuilder implements GeometryBuilder<RequestPolygon> {

    @Override
    public Geometry create(RequestPolygon parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        Coordinate[] coordinates = new Coordinate[parameters.getLons().length];
        for (int i=0;i<parameters.getLons().length; i++ )
            coordinates[i] = new Coordinate(parameters.getLons()[i], parameters.getLats()[i]);

        LinearRing linearRing = factory.createLinearRing(coordinates);

        return factory.createPolygon(linearRing,null);
    }
}
