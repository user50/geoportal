package org.w2fc.geoportal.reports;

import java.util.Date;

/**
 * @author Yevhen
 */
public class ReportRaw {
    private Date date;
    private String pid;
    private String login;

    public ReportRaw(Date date, String pid, String login) {
        this.date = date;
        this.pid = pid;
        this.login = login;
    }

    public Date getDate() {
        return date;
    }

    public String getPid() {
        return pid;
    }

    public String getLogin() {
        return login;
    }
}
