package com.antoniotari.reactiveampache.models;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/19/16.
 */
@Root
public class ArtistsResponse extends BaseResponse {

    @ElementList (inline = true, required = false)
    private List<Artist> artists;

    public List<Artist> getArtists() {
        return artists;
    }
}
