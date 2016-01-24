package org.w2fc.geoportal.ws.geometry;

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
        if ((parameters.getLat() == null|| parameters.getLon() == null)&& parameters.getAddress() == null)
            throw new MissingParameterException("Coordinates or address must be present");

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        Coordinate coordinate;

        if (parameters.getLat() == null || parameters.getLon() == null)
            coordinate = geoCoder.getCoordinatesByAddress(parameters.getAddress());
        else
            coordinate = new Coordinate(parameters.getLon(), parameters.getLat());

        return factory.createPoint(coordinate );
    }
}
