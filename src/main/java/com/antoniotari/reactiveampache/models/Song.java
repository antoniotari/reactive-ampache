package com.antoniotari.reactiveampache.models;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class Song {

    @Attribute (name = "id", required = false)
    String id;

    @Element (name = "title", required = false)
    String title;

    @Element (name = "artist", required = false)
    InfoTag artist;

    @Element (name = "album", required = false)
    InfoTag album;

    @ElementList (inline = true, required = false)
    List<Tag> tag;

    @Element (name = "track", required = false)
    int track;

    @Element (name = "time", required = false)
    int time;

    @Element (name = "year", required = false)
    String year;

    @Element (name = "bitrate", required = false)
    int bitrate;

    @Element (name = "mode", required = false)
    String mode;

    @Element (name = "mime", required = false)
    String mime;

    @Element (name = "url", required = false)
    String url;

    @Element (name = "size", required = false)
    int size;

    @Element (name = "mbid", required = false)
    String mbid;

    @Element (name = "album_mbid", required = false)
    String album_mbid;

    @Element (name = "artist_mbid", required = false)
    String artist_mbid;

    @Element (name = "art", required = false)
    String art;

    @Element (name = "preciserating", required = false)
    float preciserating;

    @Element (name = "rating", required = false)
    float rating;

    @Element (name = "averagerating", required = false)
    float averagerating;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public InfoTag getArtist() {
        return artist;
    }

    public InfoTag getAlbum() {
        return album;
    }

    public List<Tag> getTag() {
        return tag;
    }

    public int getTrack() {
        return track;
    }

    public int getTime() {
        return time;
    }

    public String getYear() {
        return year;
    }

    public int getBitrate() {
        return bitrate;
    }

    public String getMode() {
        return mode;
    }

    public String getMime() {
        return mime;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public String getMbid() {
        return mbid;
    }

    public String getAlbum_mbid() {
        return album_mbid;
    }

    public String getArtist_mbid() {
        return artist_mbid;
    }

    public String getArt() {
        return art;
    }

    public float getPreciserating() {
        return preciserating;
    }

    public float getRating() {
        return rating;
    }

    public float getAveragerating() {
        return averagerating;
    }
}
