package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Created by antonio tari on 2016-06-21.
 */
public class Playlist implements Parcelable, AmpacheModel, Taggable {
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

    @ElementList (inline = true, required = false)
    List<Tag> tag;

    public Playlist() {}

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
        dest.writeString(owner);
        dest.writeInt(items);
        dest.writeString(type);
        if (tag == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tag);
        }
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
