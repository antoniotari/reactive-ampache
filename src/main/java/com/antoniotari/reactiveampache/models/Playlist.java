package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * Created by antonio tari on 2016-06-21.
 */
public class Playlist implements Parcelable, AmpacheModel {
    @Attribute (name = "id", required = false)
    String id;

    @Element (name = "name", required = false)
    String name;

    @Element (name = "owner", required = false)
    String owner;

    @Element (name = "items", required = false)
    int items;

    @Element (name = "type", required = false)
    String type;

    public Playlist() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getItems() {
        return items;
    }

    public String getType() {
        return type;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setItems(final int items) {
        this.items = items;
    }

    protected Playlist(Parcel in) {
        id = in.readString();
        name = in.readString();
        owner = in.readString();
        items = in.readInt();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(owner);
        dest.writeInt(items);
        dest.writeString(type);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
