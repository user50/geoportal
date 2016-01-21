package org.w2fc.geoportal.ws.geocoder;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.w2fc.geoportal.ws.http.HttpService;

/**
 * @author Yevhen
 */
public class YandexGeoCoderFactory implements GeoCoderFactory{

    private static final String GEOCODE_YANDEX_HOST = "geocode-maps.yandex.ru";

    public YandexGeoCoder create(){
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost geocodeHost = new HttpHost(GEOCODE_YANDEX_HOST, 80);
        cm.setMaxPerRoute(new HttpRoute(geocodeHost), 50);
        HttpClient httpClient = new DefaultHttpClient(cm);

        return new YandexGeoCoder(new HttpService(httpClient));
    }
}
