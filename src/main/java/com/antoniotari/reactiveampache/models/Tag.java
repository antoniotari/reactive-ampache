package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class Tag implements Parcelable, AmpacheModel {
    @Attribute (name = "id")
    int id;

    @Attribute (name = "count", required = false)
    int count;

    @Text (required = false)
    String tag;

    @Override
    public String getName() {
        return tag;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    public int getCount() {
        return count;
    }

    public String getTag() {
        return tag;
    }

    public Tag() {

    }

    protected Tag(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        tag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeString(tag);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}