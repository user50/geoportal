package org.w2fc.geoportal.ws.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

    List<String> successions = new ArrayList<String>();
    Map<String,String> fails = new HashMap<String, String>();

    public Message(List<String> successions, Map<String, String> fails) {
        this.successions = successions;
        this.fails = fails;
    }

    public Message() {
    }

    public List<String> getSuccessions() {
        return successions;
    }

    public void setSuccessions(List<String> successions) {
        this.successions = successions;
    }

    public Map<String, String> getFails() {
        return fails;
    }

    public void setFails(Map<String, String> fails) {
        this.fails = fails;
    }
}
