package org.w2fc.geoportal.ws.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * @author Yevhen
 */
public class DecodedAddressYandexApiResponseHandler implements HttpResponseHandler<Coordinate> {

    public static final String JSON_PATH_TO_COORDS = "$.response.GeoObjectCollection.featureMember[0].GeoObject.Point.pos";

    @Override
    public Coordinate handle(HttpResponse httpResponse) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();

        JSONObject responseBody = objectMapper.readValue(httpResponse.getEntity().getContent(), JSONObject.class);

        String jsonString = responseBody.toJSONString();

        String point = JsonPath.read(jsonString, JSON_PATH_TO_COORDS);

        String[] coords = point.split(" ");

        double latitude = Double.parseDouble(coords[0]);
        double longitude = Double.parseDouble(coords[1]);

        return new Coordinate(longitude, latitude);
    }
}
