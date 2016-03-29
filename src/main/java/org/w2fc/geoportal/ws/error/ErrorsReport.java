package org.w2fc.geoportal.ws.error;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorsReport {

    @XmlElement
    private List<ErrorDesc> errors;

    public ErrorsReport(List<ErrorDesc> errors) {
        this.errors = errors;
    }

    public List<ErrorDesc> getErrors() {
        return errors;
    }
}
