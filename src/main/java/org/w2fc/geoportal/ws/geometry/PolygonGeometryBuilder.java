package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.LineCoordinates;
import org.w2fc.geoportal.ws.model.RequestPolygon;

public class PolygonGeometryBuilder implements GeometryBuilder<RequestPolygon> {

    @Override
    public Geometry create(RequestPolygon parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        LinearRing polygon = buildLinearRing(parameters.getPointCoordinateses(), factory);
        LinearRing[] holes = buildHoles(parameters.getPolygonHoles(), factory);

        return factory.createPolygon(polygon, holes);
    }

    private LinearRing[] buildHoles(LineCoordinates[] polygonHoles, GeometryFactory factory) {
        if (polygonHoles == null || polygonHoles.length == 0 )
            return null;

        LinearRing[] holes = new LinearRing[polygonHoles.length];
        for (int i = 0; i < polygonHoles.length; i++) {
            LineCoordinates hole = polygonHoles[i];
            holes[i] = buildLinearRing(hole.getPointsCoordinates(), factory);
        }
        return holes;
    }

    private LinearRing buildLinearRing(PointCoordinates[] points, GeometryFactory factory) {
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int i=0;i<points.length; i++ )
            coordinates[i] = new Coordinate(points[i].getLon(), points[i].getLat());

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
