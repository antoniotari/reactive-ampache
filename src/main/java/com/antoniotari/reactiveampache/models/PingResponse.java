package com.antoniotari.reactiveampache.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/19/16.
 */
@Root
public class PingResponse extends BaseResponse {
    @Element (name="session_expire", required = false)
    private String session_expire;

    @Element (name="server")
    private String server;

    @Element (name="version")
    private String version;

    @Element (name="compatible")
    private String compatible;

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
}
