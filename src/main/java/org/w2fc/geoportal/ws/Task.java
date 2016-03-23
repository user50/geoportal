package org.w2fc.geoportal.ws;

public abstract class Task implements Runnable {

    private boolean errors = false;


    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }
}
