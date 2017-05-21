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
public class Album implements Parcelable, AmpacheModel, Sortable, Taggable {
    @Attribute (name = "id", required = false)
    private String id;

    @Element (name = "name", required = false)
    private String name;

    @Element (name = "artist", required = false)
    private InfoTag artist;

    @Element (name = "year", required = false)
    private String year;

    @Element (name = "tracks", required = false)
    private int tracks;

    @Element (name = "disk", required = false)
    private float disk;

    @Element (name = "art", required = false)
    private String art;

    @Element (name = "preciserating", required = false)
    private float preciserating;

    @Element (name = "rating", required = false)
    private float rating;

    @Element (name = "averagerating", required = false)
    private float averagerating;

    @ElementList (inline = true, required = false)
    List<Tag> tag;

    public Album(){

    }

    public String getId() {
        return id;
    }

    @Override
    public List<Tag> getTags() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public InfoTag getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public int getTracks() {
        return tracks;
    }

    public float getDisk() {
        return disk;
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



    protected Album(Parcel in) {
        id = in.readString();
        name = in.readString();
        artist = (InfoTag) in.readValue(InfoTag.class.getClassLoader());
        year = in.readString();
        tracks = in.readInt();
        disk = in.readFloat();
        art = in.readString();
        preciserating = in.readFloat();
        rating = in.readFloat();
        averagerating = in.readFloat();
        if (in.readByte() == 0x01) {
            tag = new ArrayList<Tag>();
            in.readList(tag, Tag.class.getClassLoader());
        } else {
            tag = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeValue(artist);
        dest.writeString(year);
        dest.writeInt(tracks);
        dest.writeFloat(disk);
        dest.writeString(art);
        dest.writeFloat(preciserating);
        dest.writeFloat(rating);
        dest.writeFloat(averagerating);
        if (tag == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tag);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public String getSortName() {
        return getName();
    }

    @Override
    public String getSortYear() {
        return getYear();
    }

    @Override
    public String getSortTag() {
        return getArtist().getName();
    }
}
