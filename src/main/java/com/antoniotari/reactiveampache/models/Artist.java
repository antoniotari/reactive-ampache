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
public class Artist implements Parcelable, AmpacheModel, Taggable {
    @Attribute (name = "id", required = false)
    private String id;

    @Element (name = "name", required = false)
    private String name;

    @ElementList (inline = true, required = false)
    private List<Tag> tag;

    @Element (name = "albums", required = false)
    private int albums;

    @Element (name = "songs", required = false)
    private int songs;

    @Element (name = "preciserating", required = false)
    private float preciserating;

    @Element (name = "rating", required = false)
    private float rating;

    @Element (name = "averagerating", required = false)
    private float averagerating;

    /**
     * default constructor to avoid Constructor not matched error
     * during xml parsing
     */
    public Artist() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<Tag> getTags() {
        return tag;
    }

    public int getAlbums() {
        return albums;
    }

    public int getSongs() {
        return songs;
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


    protected Artist(Parcel in) {
        id = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            tag = new ArrayList<Tag>();
            in.readList(tag, Tag.class.getClassLoader());
        } else {
            tag = null;
        }
        albums = in.readInt();
        songs = in.readInt();
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
        dest.writeString(name);
        if (tag == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tag);
        }
        dest.writeInt(albums);
        dest.writeInt(songs);
        dest.writeFloat(preciserating);
        dest.writeFloat(rating);
        dest.writeFloat(averagerating);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
