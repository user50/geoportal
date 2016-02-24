package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.reports.Report1Controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author yevhenlozov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/SOAPOperationResponse")
public class SOAPOperationResponse {

    @XmlElement(name = "pid", required=true)
    private String pid;

    @XmlElement(name = "reportLink", required=true)
    private String reportLink;

    public SOAPOperationResponse() {
    }

    public SOAPOperationResponse(String pid) {
        this.pid = pid;
        this.reportLink = "/report" + Report1Controller.REPORT_URL_PATTERN.replace("{pid}", pid);
    }

    public String getPid() {
        return pid;
    }

    public String getReportLink() {
        return reportLink;
    }
}
