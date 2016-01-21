package org.w2fc.geoportal.ws.geocoder;

import com.vividsolutions.jts.geom.Coordinate;
import org.w2fc.geoportal.ws.http.DecodedAddressYandexApiResponseHandler;
import org.w2fc.geoportal.ws.http.GeocodeAddressYandexApiRequest;
import org.w2fc.geoportal.ws.http.HttpService;

import java.io.IOException;

/**
 * @author Yevhen
 */
public class YandexGeoCoder implements GeoCoder{

    private HttpService httpService;

    public YandexGeoCoder(HttpService httpService) {
        this.httpService = httpService;
    }

    public Coordinate getCoordinatesByAddress(String address)  {
        Coordinate coordinate = null;
        try {
            coordinate = httpService.execute(new GeocodeAddressYandexApiRequest(address),
                    new DecodedAddressYandexApiResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException("Unable to geocode address");
        }

        return coordinate;
    }
}
