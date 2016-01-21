package org.w2fc.geoportal.ws.http;

import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by user50 on 26.05.2015.
 */
public interface HttpResponseHandler<T> {

    T handle(HttpResponse httpResponse) throws IOException;

}
