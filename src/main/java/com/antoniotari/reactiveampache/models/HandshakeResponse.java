package com.antoniotari.reactiveampache.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/10/16.
 */
@Root
public class HandshakeResponse extends BaseResponse {

    @Element (name="auth")
    private String auth;

    @Element(name="api")
    private int api;

    @Element (name="session_expire")
    private String session_expire;

    @Element (name="update")
    private String update;

    @Element (name="add")
    private String add;

    @Element (name="clean")
    private String clean;

    @Element(name="songs")
    private int songs;

    @Element(name="albums")
    private int albums;

    @Element(name="artists")
    private int artists;

    @Element(name="playlists")
    private int playlists;

    @Element(name="videos")
    private int videos;

    @Element(name="catalogs")
    private int catalogs;

    public String getAuth() {
        return auth;
    }

    public int getApi() {
        return api;
    }

    public String getSession_expire() {
        return session_expire;
    }

    public String getUpdate() {
        return update;
    }

    public String getAdd() {
        return add;
    }

    public String getClean() {
        return clean;
    }

    public int getSongs() {
        return songs;
    }

    public int getAlbums() {
        return albums;
    }

    public int getArtists() {
        return artists;
    }

    public int getPlaylists() {
        return playlists;
    }

    public int getVideos() {
        return videos;
    }

    public int getCatalogs() {
        return catalogs;
    }

    public void setSession_expire(final String session_expire) {
        this.session_expire = session_expire;
    }
}