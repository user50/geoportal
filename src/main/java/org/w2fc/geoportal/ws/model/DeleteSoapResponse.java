package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.ws.error.ErrorDesc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author Yevhen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/DeleteSoapResponse")
public class DeleteSoapResponse {

    @XmlElement
    private String status;

    @XmlElement
    private List<ErrorDesc> errors;

    public DeleteSoapResponse() {
    }

    public DeleteSoapResponse(String status) {
        this.status = status;
    }

    public DeleteSoapResponse(String status, List<ErrorDesc> errors) {
        this.status = status;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public List<ErrorDesc> getErrors() {
        return errors;
    }
}
