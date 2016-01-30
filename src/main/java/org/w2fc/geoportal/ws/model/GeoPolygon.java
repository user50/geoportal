package org.w2fc.geoportal.ws.model;

import java.util.List;

public class GeoPolygon {

    private List<LineCoordinates> lineStrings;

    public GeoPolygon(List<LineCoordinates> lineStrings) {
        this.lineStrings = lineStrings;
    }

    public List<LineCoordinates> getLineStrings() {
        return lineStrings;
    }
}
