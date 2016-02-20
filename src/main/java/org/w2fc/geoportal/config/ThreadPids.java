package org.w2fc.geoportal.config;

import java.util.HashMap;
import java.util.Map;

public enum ThreadPids {

    INSTANCE;

    private Map<Long,String> threadToPid = new HashMap<Long, String>();

    synchronized public void put(long id, String pid)
    {
        threadToPid.put(id, pid);
    }

    synchronized public String getPid(long id)
    {
        return threadToPid.get(id);
    }

    synchronized public void remove(long id)
    {
        threadToPid.remove(id);
    }
}
