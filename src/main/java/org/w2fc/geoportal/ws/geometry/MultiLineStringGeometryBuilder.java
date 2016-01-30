package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.model.GeoLineString;
import org.w2fc.geoportal.ws.model.MultiLineString;
import org.w2fc.geoportal.ws.model.PointCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yevhen
 */
public class MultiLineStringGeometryBuilder implements GeometryBuilder<MultiLineString> {

    @Override
    public Geometry create(MultiLineString parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        List<LineString> lineStrings = new ArrayList<LineString>();

        for (GeoLineString geoLineString : parameters.getLineStrings()) {
            List<PointCoordinates> listPoints = geoLineString.getPointsCoordinates();
            lineStrings.add(buildLineString(listPoints, factory));
        }

        return factory.createMultiLineString(lineStrings.toArray(new LineString[lineStrings.size()]));
    }

    private LineString buildLineString(List<PointCoordinates> points, GeometryFactory factory) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        for (PointCoordinates point : points) {
            coordinates.add(new Coordinate(point.getLon(), point.getLat()));
        }

        return factory.createLinearRing(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

}
