package org.w2fc.geoportal.ws.geometry.builder;

import com.vividsolutions.jts.geom.*;
import org.w2fc.geoportal.ws.exception.MissingParameterException;
import org.w2fc.geoportal.ws.geocoder.GeoCoder;
import org.w2fc.geoportal.ws.model.RequestPoint;

public class PointGeometryBuilder implements GeometryBuilder<RequestPoint> {

    private GeoCoder geoCoder;

    public PointGeometryBuilder(GeoCoder geoCoder) {
        this.geoCoder = geoCoder;
    }

    @Override
    public Geometry create(RequestPoint parameters) {
        if (parameters.getPointCoordinates() == null && parameters.getAddress() == null)
            throw new MissingParameterException("Coordinates or address must be present");

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        Coordinate coordinate;

        if (parameters.getPointCoordinates() == null)
            coordinate = geoCoder.getCoordinatesByAddress(parameters.getAddress());
        else
            coordinate = new Coordinate(parameters.getPointCoordinates().getLon(), parameters.getPointCoordinates().getLat());

        return factory.createPoint(coordinate );
    }
}
