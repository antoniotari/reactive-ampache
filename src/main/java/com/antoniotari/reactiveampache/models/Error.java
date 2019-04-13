package com.antoniotari.reactiveampache.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class Error {

    @Attribute (name = "code", required = false)
    private String code;

    @Text
    private String error;

    private Throwable throwable;

    public Error() {

    }

    public Error(String code, Throwable throwable) {
        this.code = code;
        this.error = throwable.getLocalizedMessage();
        this.throwable = throwable;
    }

    public Error(String code, String error) {
        this.code = code;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
