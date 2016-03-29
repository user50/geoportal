package org.w2fc.geoportal.ws.error;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDesc {

    @XmlElement
    private int code;

    @XmlElement
    private String guid;

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
