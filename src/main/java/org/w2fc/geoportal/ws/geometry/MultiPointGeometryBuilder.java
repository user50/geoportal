package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.w2fc.geoportal.ws.model.MultiPoint;

/**
 * @author Yevhen
 */
public class MultiPointGeometryBuilder implements GeometryBuilder<MultiPoint> {

    @Override
    public Geometry create(MultiPoint parameters) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

        int countOfPoints = parameters.getPointsCoordinates().length;
        Coordinate[] coordinates = new Coordinate[countOfPoints];

        for (int i = 0; i < countOfPoints; i++) {
            coordinates[i] = new Coordinate(parameters.getPointsCoordinates()[i].getLon(), parameters.getPointsCoordinates()[i].getLat());
        }

        return factory.createMultiPoint(coordinates);
    }
}
