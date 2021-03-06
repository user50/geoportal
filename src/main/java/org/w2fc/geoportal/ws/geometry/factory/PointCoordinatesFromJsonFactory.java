package org.w2fc.geoportal.ws.geometry.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w2fc.geoportal.ws.model.PointCoordinates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yevhenlozov
 */
public class PointCoordinatesFromJsonFactory {

    List<PointCoordinates> create(String jsonString) {
        if (jsonString == null || jsonString.isEmpty())
            throw new IllegalArgumentException("JSON string with coordinates can not be null or empty");

        ObjectMapper objectMapper = new ObjectMapper();

        Double[][] list;
        try {
            list = objectMapper.readValue(jsonString, Double[][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse coordinates");
        }

        List<PointCoordinates> results = new ArrayList<PointCoordinates>();

        for (Double[] coords : list) {
            results.add(new PointCoordinates(coords[0], coords[1]));
        }

        return results;
    }
}
