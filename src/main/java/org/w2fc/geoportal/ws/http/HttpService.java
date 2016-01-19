package org.w2fc.geoportal.ws.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * Created by user50 on 26.05.2015.
 */
public class HttpService {

    private HttpClient httpClient;
    private HttpHost host;

    public HttpService(HttpClient httpClient, HttpHost host) {
        this.httpClient = httpClient;
        this.host = host;
    }

    public <T> T execute(HttpRequestProvider httpRequestProvider, HttpResponseHandler<T> responseHandler) throws IOException {

        HttpRequestBase httpRequest = httpRequestProvider.getRequest();

        HttpResponse response = httpClient.execute(host, httpRequest);

        return responseHandler.handle(response);
    }
}
