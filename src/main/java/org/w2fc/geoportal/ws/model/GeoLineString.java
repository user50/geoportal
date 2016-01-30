package org.w2fc.geoportal.ws.model;

import java.util.List;

/**
 * @author Yevhen
 */
public class GeoLineString {

    private List<PointCoordinates> pointsCoordinates;

    public GeoLineString(List<PointCoordinates> pointsCoordinates) {
        this.pointsCoordinates = pointsCoordinates;
    }

    public List<PointCoordinates> getPointsCoordinates() {
        return pointsCoordinates;
    }
}
