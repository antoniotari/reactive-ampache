package com.antoniotari.reactiveampache.models;

import java.util.List;

import org.simpleframework.xml.ElementList;

/**
 * Created by antoniotari on 2016-06-21.
 */
public class PlaylistsResponse extends BaseResponse {

    @ElementList (inline = true, required = false)
    private List<Playlist> playlists;

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    @Override
    public List<? extends AmpacheModel> getItems() {
        return getPlaylists();
    }
}
