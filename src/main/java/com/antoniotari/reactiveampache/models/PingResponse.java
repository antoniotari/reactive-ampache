package com.antoniotari.reactiveampache.models;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/19/16.
 */
@Root
public class PingResponse extends BaseResponse {
    @Element (name="session_expire", required = false)
    private String session_expire;

    @Element (name="server", required = false)
    private String server;

    @Element (name="version", required = false)
    private String version;

    @Element (name="compatible", required = false)
    private String compatible;

    public PingResponse() {}

    public String getSession_expire() {
        return session_expire;
    }

    public String getServer() {
        return server;
    }

    public String getVersion() {
        return version;
    }

    public String getCompatible() {
        return compatible;
    }

    @Override
    public List<? extends AmpacheModel> getItems() {
        return null;
    }
}
