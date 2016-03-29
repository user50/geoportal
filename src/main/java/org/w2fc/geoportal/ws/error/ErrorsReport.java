package org.w2fc.geoportal.ws.error;

import java.util.List;

public class ErrorsReport {

    private List<ErrorDesc> errors;

    public ErrorsReport(List<ErrorDesc> errors) {
        this.errors = errors;
    }

    public List<ErrorDesc> getErrors() {
        return errors;
    }
}
