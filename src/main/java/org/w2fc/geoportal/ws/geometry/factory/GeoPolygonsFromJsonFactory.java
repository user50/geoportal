package org.w2fc.geoportal.ws.geometry.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w2fc.geoportal.ws.model.GeoPolygon;
import org.w2fc.geoportal.ws.model.LineCoordinates;
import org.w2fc.geoportal.ws.model.PointCoordinates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoPolygonsFromJsonFactory
{
    public List<GeoPolygon> create(String json){
        ObjectMapper objectMapper = new ObjectMapper();

        Double[][][][] coords;
        try {
            coords = objectMapper.readValue(json, Double[][][][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse coordinates");
        }

        List<GeoPolygon> polygons = new ArrayList<GeoPolygon>();

        for (Double[][][] polygonCoord : coords) {
            List<LineCoordinates> lineCoordinates = new ArrayList<LineCoordinates>();

            for (Double[][] holeCoords : polygonCoord) {
                PointCoordinates[] points = new PointCoordinates[holeCoords.length];
                for (int i = 0; i < holeCoords.length; i++) {
                    points[i] = new PointCoordinates(holeCoords[i][0], holeCoords[i][1]);
                }
                lineCoordinates.add(new LineCoordinates(points));
            }

            polygons.add(new GeoPolygon(lineCoordinates));
        }

        return polygons;
    }
}
