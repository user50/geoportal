package org.w2fc.geoportal.ws.geometry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.PolygonHole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yevhenlozov
 */
public class PolygonHoleFromJsonFactory {

    public List<PolygonHole> create(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        Double[][][] holesCoords;
        try {
            holesCoords = objectMapper.readValue(jsonString, Double[][][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse coordinates");
        }

        List<PolygonHole> results = new ArrayList<PolygonHole>();

        for (Double[][] holeCoords : holesCoords) {
            PointCoordinates[] points = new PointCoordinates[holeCoords.length];
            for (int i = 0; i < holeCoords.length; i++) {
                points[i] = new PointCoordinates(holeCoords[i][0], holeCoords[i][1]);
            }
            results.add(new PolygonHole(points));
        }

        return results;
    }
}
