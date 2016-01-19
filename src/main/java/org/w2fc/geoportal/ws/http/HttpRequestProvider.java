package org.w2fc.geoportal.ws.http;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * Created by user50 on 26.05.2015.
 */
public interface HttpRequestProvider {

    HttpRequestBase getRequest() throws IOException;

}
