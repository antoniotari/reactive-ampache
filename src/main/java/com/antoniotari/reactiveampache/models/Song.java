package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class Song implements Parcelable, AmpacheModel {

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

    public Song(){}

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return title;
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

    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = (InfoTag) in.readValue(InfoTag.class.getClassLoader());
        album = (InfoTag) in.readValue(InfoTag.class.getClassLoader());
        if (in.readByte() == 0x01) {
            tag = new ArrayList<Tag>();
            in.readList(tag, Tag.class.getClassLoader());
        } else {
            tag = null;
        }
        track = in.readInt();
        time = in.readInt();
        year = in.readString();
        bitrate = in.readInt();
        mode = in.readString();
        mime = in.readString();
        url = in.readString();
        size = in.readInt();
        mbid = in.readString();
        album_mbid = in.readString();
        artist_mbid = in.readString();
        art = in.readString();
        preciserating = in.readFloat();
        rating = in.readFloat();
        averagerating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeValue(artist);
        dest.writeValue(album);
        if (tag == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tag);
        }
        dest.writeInt(track);
        dest.writeInt(time);
        dest.writeString(year);
        dest.writeInt(bitrate);
        dest.writeString(mode);
        dest.writeString(mime);
        dest.writeString(url);
        dest.writeInt(size);
        dest.writeString(mbid);
        dest.writeString(album_mbid);
        dest.writeString(artist_mbid);
        dest.writeString(art);
        dest.writeFloat(preciserating);
        dest.writeFloat(rating);
        dest.writeFloat(averagerating);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
