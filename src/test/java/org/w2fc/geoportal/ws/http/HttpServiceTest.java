package org.w2fc.geoportal.ws.http;

import com.vividsolutions.jts.geom.Coordinate;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Test;

public class HttpServiceTest {


    @Test
    public void testExecute() throws Exception {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
       // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost yandexGeocoder = new HttpHost("geocode-maps.yandex.ru", 80);

        cm.setMaxPerRoute(new HttpRoute(yandexGeocoder), 50);

        HttpClient httpClient = new DefaultHttpClient(cm);

        HttpService httpService = new HttpService(httpClient);
        Coordinate coordinate = httpService.execute(new GeocodeAddressYandexApiRequest("Тверская 6"),
                            new DecodedAddressYandexApiResponseHandler());

        System.out.println(coordinate);
    }
}