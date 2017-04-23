package com.antoniotari.reactiveampache.models;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/19/16.
 */
@Root
public class AlbumsResponse extends BaseResponse {

    @ElementList (inline = true, required = false)
    private List<Album> albums;

    public List<Album> getAlbums() {
        return albums;
    }

    @Override
    public List<? extends AmpacheModel> getItems() {
        return getAlbums();
    }
}
