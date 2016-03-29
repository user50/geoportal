package org.w2fc.geoportal.ws.async;

import java.util.HashMap;
import java.util.Map;

public enum  SOAPProcessStatus {

    INSTANCE;

    Map<String, Object> statusByPid = new HashMap<String, Object>();

    synchronized public void put(String pid, Object status){
        statusByPid.put(pid, status);
    }

    public Object get(String pid){
        return statusByPid.get(pid);
    }
}
