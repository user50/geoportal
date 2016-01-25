package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.PolygonHole;
import org.w2fc.geoportal.ws.model.RequestPolygon;

public class PolygonGeometryBuilder implements GeometryBuilder<RequestPolygon> {

    @Override
    public Geometry create(RequestPolygon parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
        LinearRing polygon = buildLinearRing(parameters.getLats(), parameters.getLons(), factory);
        LinearRing[] holes = buildHoles(parameters.getPolygonHoles(), factory);

        return factory.createPolygon(polygon, holes);
    }

    private LinearRing[] buildHoles(PolygonHole[] polygonHoles, GeometryFactory factory) {
        if (polygonHoles == null || polygonHoles.length == 0 )
            return null;

        LinearRing[] holes = new LinearRing[polygonHoles.length];
        for (int i = 0; i < polygonHoles.length; i++) {
            PolygonHole hole = polygonHoles[i];
            holes[i] = buildLinearRing(hole.getLats(), hole.getLons(), factory);
        }
        return holes;
    }

    private LinearRing buildLinearRing(Double[] lats, Double[] lons, GeometryFactory factory) {
        validateCoordinates(lats, lons);

        Coordinate[] coordinates = new Coordinate[lons.length];
        for (int i=0;i<lons.length; i++ )
            coordinates[i] = new Coordinate(lons[i], lats[i]);

        return factory.createLinearRing(coordinates);
    }

    private void validateCoordinates(Double[] lats, Double[] lons) {
        if (lats.length != lons.length)
            throw new InvalidGeometryException("Counts of latitudes and longitudes must be equal");

        if (lats.length < 4 || lons.length < 4)
            throw new InvalidGeometryException("At least coordinates for four points must be specified");

        if (!lats[0].equals(lats[lats.length-1]) ||
                !lons[0].equals(lons[lons.length-1]))
            throw new InvalidGeometryException("Points do not form a closed polygon");
    }
}
