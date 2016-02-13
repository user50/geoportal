package org.w2fc.geoportal.ws.async;

import java.util.HashMap;
import java.util.Map;

public enum  SOAPProcessStatus {

    INSTANCE;

    Map<String, String> statusByPid = new HashMap<String, String>();

    synchronized public void put(String pid, String status){
        statusByPid.put(pid, status);
    }

    public String get(String pid){
        return statusByPid.get(pid);
    }
}
