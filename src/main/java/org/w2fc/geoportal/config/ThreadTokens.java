package org.w2fc.geoportal.config;

import java.util.HashMap;
import java.util.Map;

public enum ThreadTokens {

    INSTANCE;

    Map<Long,String> threadToToken = new HashMap<Long, String>();

    synchronized public void put(long id, String token)
    {
        threadToToken.put(id, token);
    }

    synchronized public String getToken(long id)
    {
        return threadToToken.get(id);
    }

    synchronized public void remove(long id)
    {
        threadToToken.remove(id);
    }
}
