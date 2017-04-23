package com.antoniotari.reactiveampache.models;

import java.util.List;

import com.antoniotari.reactiveampache.utils.MD5;
import com.antoniotari.reactiveampache.utils.SerializeUtils;

import org.simpleframework.xml.Element;

/**
 * Created by antonio.tari on 5/19/16.
 */
public abstract class BaseResponse {

    @Element (name = "error", required = false)
    private Error error;

    public Error getError() {
        return error;
    }

    public String toJson() {
        return new SerializeUtils().toJsonString(this);
    }

    @Override
    public String toString() {
        String toString = toJson();
        if(toString == null) {
            toString = super.toString();
        }
        return toString;
    }

    @Override
    public boolean equals(final Object other) {
        if(other==null) return false;
        if(!(other instanceof BaseResponse)) return false;
        BaseResponse otherBs = (BaseResponse) other;
        return MD5.md5(toJson()).equals(MD5.md5(otherBs.toJson()));
    }

    public abstract List<? extends AmpacheModel> getItems();
}
