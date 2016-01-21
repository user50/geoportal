package org.w2fc.geoportal.ws.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Yevhen
 */
public class GeocodeAddressYandexApiRequest implements HttpRequestProvider {

    private static final String BASE_URI = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=";
    private static final String UTF8_ENCODING = "UTF-8";

    private String address;

    public GeocodeAddressYandexApiRequest(String address) {
        this.address = address;
    }

    @Override
    public HttpRequestBase getRequest() throws IOException{
        String encodedAddress = URLEncoder.encode(address, UTF8_ENCODING);

        return new HttpGet(BASE_URI + encodedAddress);
    }
}
