package com.antoniotari.reactiveampache.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.antoniotari.reactiveampache.utils.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by antoniotari on 2017-05-21.
 */
@Root
public class TagsResponse extends BaseResponse {

    @ElementList (inline = true, required = false)
    private List<Tag> tag;

    public Tags getTags() {
        Log.log(tag.size());
        return Tags.tagsFactory(tag);
    }

    public List<Tag> getTagList() {
        return tag;
    }

    @Override
    public List<? extends AmpacheModel> getItems() {
        return tag;
    }

    public List<Tag> getTag() {
        return tag;
    }


    static class Tag implements AmpacheModel, Parcelable {
        @Attribute (name = "id", required = false)
        String id;

        @Element (name = "albums", required = false)
        int albumCount;

        @Element (name = "artists", required = false)
        int artistCount;

        @Element (name = "songs", required = false)
        int songCount;

        @Element (name = "videos", required = false)
        int videoCount;

        @Element (name = "playlists", required = false)
        int playlistCount;

        @Element (name = "stream", required = false)
        int streamCount;

        @Element (name = "name", required = false)
        String name;

        public Tag() {
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getId() {
            return String.valueOf(id);
        }

        public String getTag() {
            return name;
        }



        protected Tag(Parcel in) {
            albumCount = in.readInt();
            artistCount = in.readInt();
            songCount = in.readInt();
            videoCount = in.readInt();
            playlistCount = in.readInt();
            streamCount = in.readInt();
            name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(albumCount);
            dest.writeInt(artistCount);
            dest.writeInt(songCount);
            dest.writeInt(videoCount);
            dest.writeInt(playlistCount);
            dest.writeInt(streamCount);
            dest.writeString(name);
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
}
