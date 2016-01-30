package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.InvalidGeometryException;
import org.w2fc.geoportal.ws.model.*;
import org.w2fc.geoportal.ws.model.MultiPolygon;

import java.util.ArrayList;
import java.util.List;

public class MultiPolygonBuilder implements GeometryBuilder<MultiPolygon> {

    GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    @Override
    public Geometry create(MultiPolygon parameters) {
        List<Polygon> polygons = new ArrayList<Polygon>();

        for (GeoPolygon geoPolygon : parameters.getPolygons())
            polygons.add(create(geoPolygon));

        return factory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
    }

    public Polygon create(GeoPolygon poligon) {
        LinearRing polygon = buildLinearRing(poligon.getLineStrings().get(0).getPointsCoordinates(), factory);
        LinearRing[] holes = buildHoles(poligon.getLineStrings().subList(1, poligon.getLineStrings().size()).toArray(new LineCoordinates[poligon.getLineStrings().size()-1]), factory);

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

}
