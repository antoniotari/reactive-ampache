package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by antonio.tari on 5/19/16.
 */
@Root
public class SongsResponse extends BaseResponse implements Parcelable{

    @ElementList (inline = true, required = false)
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public SongsResponse(){
    }

    protected SongsResponse(Parcel in) {
        if (in.readByte() == 0x01) {
            songs = new ArrayList<Song>();
            in.readList(songs, Song.class.getClassLoader());
        } else {
            songs = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (songs == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(songs);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SongsResponse> CREATOR = new Parcelable.Creator<SongsResponse>() {
        @Override
        public SongsResponse createFromParcel(Parcel in) {
            return new SongsResponse(in);
        }

        @Override
        public SongsResponse[] newArray(int size) {
            return new SongsResponse[size];
        }
    };
}
