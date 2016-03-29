package org.w2fc.geoportal.ws.async;

import org.w2fc.geoportal.ws.error.ErrorDesc;

import java.util.ArrayList;
import java.util.List;

public abstract class Task implements Runnable {

    private List<ErrorDesc> errors = new ArrayList<ErrorDesc>();

    public void add(ErrorDesc error) {
        errors.add(error);
    }

    public List<ErrorDesc> getErrors() {
        return errors;
    }


}
