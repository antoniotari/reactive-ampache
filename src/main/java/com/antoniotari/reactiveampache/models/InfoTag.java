package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class InfoTag implements Parcelable, AmpacheModel {

    @Attribute (name = "id")
    private String id;

    @Text (required=false)
    private String name;

    public InfoTag() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    protected InfoTag(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<InfoTag> CREATOR = new Parcelable.Creator<InfoTag>() {
        @Override
        public InfoTag createFromParcel(Parcel in) {
            return new InfoTag(in);
        }

        @Override
        public InfoTag[] newArray(int size) {
            return new InfoTag[size];
        }
    };
}
