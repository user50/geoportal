package org.w2fc.geoportal.ws.error;

public class ErrorDesc {

    int code;
    String guid;

    public ErrorDesc(int code, String guid) {
        this.code = code;
        this.guid = guid;
    }

    public int getCode() {
        return code;
    }

    public String getGuid() {
        return guid;
    }
}
