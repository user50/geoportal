package org.w2fc.geoportal.ws.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * @author Yevhen
 */
public class DecodedAddressYandexApiResponseHandler implements HttpResponseHandler<Coordinate> {

    @Override
    public Coordinate handle(HttpResponse httpResponse) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();

        JSONObject responceBody = objectMapper.readValue(httpResponse.getEntity().getContent(), JSONObject.class);

        //todo getting coordinates from response json

        double latitude = 0;
        double longitude = 0;

        return new Coordinate(longitude, latitude);
    }
}
