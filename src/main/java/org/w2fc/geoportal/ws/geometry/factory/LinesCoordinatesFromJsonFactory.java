package org.w2fc.geoportal.ws.geometry.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w2fc.geoportal.ws.model.PointCoordinates;
import org.w2fc.geoportal.ws.model.LineCoordinates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yevhenlozov
 */
public class LinesCoordinatesFromJsonFactory {

    public List<LineCoordinates> create(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        Double[][][] holesCoords;
        try {
            holesCoords = objectMapper.readValue(jsonString, Double[][][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse coordinates");
        }

        List<LineCoordinates> results = new ArrayList<LineCoordinates>();

        for (Double[][] holeCoords : holesCoords) {
            PointCoordinates[] points = new PointCoordinates[holeCoords.length];
            for (int i = 0; i < holeCoords.length; i++) {
                points[i] = new PointCoordinates(holeCoords[i][0], holeCoords[i][1]);
            }
            results.add(new LineCoordinates(points));
        }

        return results;
    }
}
