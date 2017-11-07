package com.antoniotari.reactiveampache.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antoniotari.reactiveampache.utils.SerializeUtils;

/**
 * Created by antoniotari on 2017-05-21.
 */

public class Tags {
    private Map<String,Tag> artistTags = new HashMap<>();
    private Map<String,Tag> albumTags = new HashMap<>();
    private Map<String,Tag> songTags = new HashMap<>();
    private Map<String,Tag> playlistTags = new HashMap<>();
    private Map<String,Tag> videoTags = new HashMap<>();
    private Map<String,Tag> streamTags = new HashMap<>();
    private Map<String,Tag> everyTag = new HashMap<>();

    public static Tags tagsFactory(List<TagsResponse.Tag> tagRespList) {
        Tags tags = new Tags();
        for(TagsResponse.Tag tagResp:tagRespList) {
            if (tagResp.artistCount > 0){
                addTagToList(tags.artistTags, tagResp, tagResp.artistCount);
            }
            if (tagResp.albumCount > 0){
                addTagToList(tags.albumTags, tagResp, tagResp.albumCount);
            }
            if (tagResp.songCount > 0){
                addTagToList(tags.songTags, tagResp, tagResp.songCount);
            }
            if (tagResp.playlistCount > 0){
                addTagToList(tags.playlistTags, tagResp, tagResp.playlistCount);
            }
            if (tagResp.videoCount > 0){
                addTagToList(tags.videoTags, tagResp, tagResp.videoCount);
            }
            if (tagResp.streamCount > 0){
                addTagToList(tags.streamTags, tagResp, tagResp.streamCount);
            }
        }
        tags.everyTag.putAll(tags.artistTags);
        tags.everyTag.putAll(tags.albumTags);
        tags.everyTag.putAll(tags.songTags);
        tags.everyTag.putAll(tags.playlistTags);
        tags.everyTag.putAll(tags.videoTags);
        tags.everyTag.putAll(tags.streamTags);
        return tags;
    }

    private static void addTagToList(Map<String, Tag> tags, TagsResponse.Tag tagResp, int count) {
        Tag tag = new Tag();
        tag.count = count;
        tag.tag = tagResp.name;
        tag.id = Integer.parseInt(tagResp.id);
        tags.put(tag.getId(), tag);
    }

    public static <T extends Taggable> List<T> filterListByTag(List<T> list, String tagId) {
        if(list == null) return new ArrayList<T>();
        if(tagId == null) return new ArrayList<T>();

        ArrayList<T> returnList = new ArrayList<>();
        for(T item:list) {
            List<Tag> tags = item.getTags();
            if (tags!= null) {
                for (Tag tag:tags) {
                    if (tag.getId().equals(tagId)){
                        returnList.add(item);
                    }
                }
            }
        }

        return returnList;
    }

    public Map<String,Tag> getAlbumTags() {
        return albumTags;
    }

    public Map<String,Tag> getArtistTags() {
        return artistTags;
    }

    public Map<String,Tag> getPlaylistTags() {
        return playlistTags;
    }

    public Map<String,Tag> getSongTags() {
        return songTags;
    }

    public Map<String,Tag> getStreamTags() {
        return streamTags;
    }

    public Map<String,Tag> getVideoTags() {
        return videoTags;
    }

    private List<Tag> mapToList(Map<String,Tag> map, boolean sort) {
        // initial conditions
        if (map == null) return new ArrayList<>();
        if (map.isEmpty()) return new ArrayList<>();

        List<Tag> tagList = new ArrayList<>();
        for (Map.Entry<String, Tag> entry : map.entrySet()) {
            Tag value = entry.getValue();
            tagList.add(value);
        }
        if (sort) {
            Collections.sort(tagList, new Comparator<Tag>() {
                @Override
                public int compare(final Tag o1, final Tag o2) {
                    if (o1 == null && o2==null) return 0;
                    if (o1 == null && o2!=null) return "".compareTo(o2.getName());
                    if (o2 == null && o1!=null) return o1.getName().compareTo("");
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        return tagList;
    }

    public List<Tag> getVideoTags(boolean sort) {
        return mapToList(getVideoTags(), sort);
    }

    public List<Tag> getAlbumTags(boolean sort) {
        return mapToList(getAlbumTags(), sort);
    }

    public List<Tag> getArtistTags(boolean sort) {
        return mapToList(getArtistTags(), sort);
    }

    public List<Tag> getPlaylistTags(boolean sort) {
        return mapToList(getPlaylistTags(), sort);
    }

    public List<Tag> getSongTags(boolean sort) {
        return mapToList(getSongTags(), sort);
    }

    public List<Tag> getStreamTags(boolean sort) {
        return mapToList(getStreamTags(), sort);
    }

    public Map<String, Tag> getEveryTag() {
        return everyTag;
    }

    @Override
    public String toString() {
        return new SerializeUtils().toJsonString(this);
    }
}
