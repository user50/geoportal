package org.w2fc.geoportal.reports;

import java.util.Date;

/**
 * @author Yevhen
 */
public class ReportRaw {
    private Date date;
    private String reportFile;
    private String userName;

    public ReportRaw(Date date, String reportFile, String userName) {
        this.date = date;
        this.reportFile = reportFile;
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public String getReportFile() {
        return reportFile;
    }

    public String getUserName() {
        return userName;
    }
}
