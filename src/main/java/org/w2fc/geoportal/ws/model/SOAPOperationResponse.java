package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.reports.Report1Controller;

/**
 * @author yevhenlozov
 */
public class SOAPOperationResponse {

    private String pid;
    private String reportLink;

    public SOAPOperationResponse() {
    }

    public SOAPOperationResponse(String pid) {
        this.pid = pid;
        this.reportLink = Report1Controller.REPORT_URL_PATTERN.replace("{pid}", pid);
    }

    public String getPid() {
        return pid;
    }

    public String getReportLink() {
        return reportLink;
    }
}
