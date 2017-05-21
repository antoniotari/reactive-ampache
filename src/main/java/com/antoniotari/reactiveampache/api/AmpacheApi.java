/*
 * Reactive Ampache, a reactive Ampache library for Android
 * Copyright (C) 2016  Antonio Tari
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.antoniotari.reactiveampache.api;

import android.content.Context;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.Gson;

import com.antoniotari.reactiveampache.Exceptions.AmpacheApiException;
import com.antoniotari.reactiveampache.api.RawRequest.PlaylistType;
import com.antoniotari.reactiveampache.models.Album;
import com.antoniotari.reactiveampache.models.AlbumsResponse;
import com.antoniotari.reactiveampache.models.Artist;
import com.antoniotari.reactiveampache.models.ArtistsResponse;
import com.antoniotari.reactiveampache.models.BaseResponse;
import com.antoniotari.reactiveampache.models.HandshakeResponse;
import com.antoniotari.reactiveampache.models.PingResponse;
import com.antoniotari.reactiveampache.models.Playlist;
import com.antoniotari.reactiveampache.models.PlaylistsResponse;
import com.antoniotari.reactiveampache.models.Song;
import com.antoniotari.reactiveampache.models.SongsResponse;
import com.antoniotari.reactiveampache.models.Tags;
import com.antoniotari.reactiveampache.models.TagsResponse;
import com.antoniotari.reactiveampache.utils.FileUtil;
import com.antoniotari.reactiveampache.utils.Log;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by antonio tari on 2016-05-21.
 */
public enum AmpacheApi {
    INSTANCE;

    private static final String FILENAME_HANDSHAKE = "com.antoniotari.ampache.library.response.handshake.json";
    private static final String FILENAME_ARTISTS = "com.antoniotari.ampache.library.response.artists.json";
    private static final String FILENAME_TAGS = "com.antoniotari.ampache.library.response.tags.json";
    private static final String FILENAME_ALBUMS = "com.antoniotari.ampache.library.response.albums.json";
    private static final String FILENAME_ALBUMS_FROM_ARTIST = "ampache.library.resp.albums.%s.json";
    private static final String FILENAME_SONGS = "com.antoniotari.ampache.library.response.songs.json";
    private static final String FILENAME_SONGS_FROM_ALBUM = "ampache.library.response.songs.%s.json";
    private static final String FILENAME_PLAYLIST_SONGS = "ampache.library.response.playlis.%s.json";
    private static final String FILENAME_PLAYLISTS = "ampache.library.response.playlists.json";

    private RawRequest mRawRequest;
    private Context mContext;

    public void initSession(Context context) {
        AmpacheSession.INSTANCE.init(context);
        mContext = context.getApplicationContext();
    }

    /**
     * initialize the ampache user, use this before making any other API call
     * @param ampacheUrl        url for the ampache server
     * @param ampacheUser       ampache user username
     * @param ampachePassword   ampache user password
     * @return                  an observable that will complete if the user is valid otherwise goes onError
     */
    public Observable<AmpacheSession> initUser(final String ampacheUrl, String ampacheUser, String ampachePassword) {
        return Observable.create(new OnSubscribe<AmpacheSession>() {

            @Override
            public void call(final Subscriber<? super AmpacheSession> subscriber) {
                try {
                    if (ampachePassword == null || ampachePassword.isEmpty()) {
                        throw new Exception("invalid password");
                    }
                    if (ampacheUser == null || ampacheUser.isEmpty()) {
                        throw new Exception("invalid user name");
                    }
                    if (ampacheUrl == null || ampacheUrl.isEmpty()) {
                        throw new Exception("invalid url");
                    }

                    String ampacheUrlMod = ampacheUrl;
                    if (!ampacheUrl.endsWith("/")) {
                        ampacheUrlMod = ampacheUrl + "/";
                    }

                    // initialize the session
                    AmpacheSession.INSTANCE.setAmpachePassword(ampachePassword);
                    AmpacheSession.INSTANCE.setAmpacheUrl(ampacheUrlMod);
                    AmpacheSession.INSTANCE.setAmpacheUser(ampacheUser);
                    // initialize raw request
                    mRawRequest = new RawRequest(ampacheUrlMod, ampacheUser, ampachePassword);
                    subscriber.onNext(AmpacheSession.INSTANCE);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * use this only if the use already logged in before
     */
    public Observable<AmpacheSession> initUser() {
        return initUser(AmpacheSession.INSTANCE.getAmpacheUrl(),
                AmpacheSession.INSTANCE.getAmpacheUser(),
                AmpacheSession.INSTANCE.getAmpachePassword());
    }

    private RawRequest getRawRequest() {
        if(mRawRequest == null){
            mRawRequest = new RawRequest(AmpacheSession.INSTANCE.getAmpacheUrl(),
                    AmpacheSession.INSTANCE.getAmpacheUser(),
                    AmpacheSession.INSTANCE.getAmpachePassword());
        }
        return mRawRequest;
    }

    private <T extends BaseResponse> void call(final Subscriber subscriber, Class<T> responseClass, Callable<T> request) {
        call(subscriber, null, responseClass, request);
    }

    private <T extends BaseResponse> void call(final Subscriber subscriber, String cacheFilename,
            Class<T> responseClass, Callable<T> request) {

        try {
            T artistsResponseCached = cacheFilename != null ? getCached(cacheFilename, responseClass) : null;
            if (artistsResponseCached != null && artistsResponseCached.getError() == null) {
                Log.log("got albums from cache");
                subscriber.onNext(artistsResponseCached.getItems());
            }

            T artistsResponse = request.call();
            if (artistsResponse.getError() != null) {
                throw new AmpacheApiException(artistsResponse.getError());
            }

            if (cacheFilename == null || checkAndCache(cacheFilename, artistsResponse, artistsResponseCached)) {
                subscriber.onNext(artistsResponse.getItems());
            }

            subscriber.onCompleted();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    /**
     * before making any API call must handshake with the server
     */
    public Observable<HandshakeResponse> handshake() {
        return Observable.create(new OnSubscribe<HandshakeResponse>() {

            @Override
            public void call(final Subscriber<? super HandshakeResponse> subscriber) {
                try {

                    HandshakeResponse handshakeResponseCached = getCached(FILENAME_HANDSHAKE, HandshakeResponse.class);
                    HandshakeResponse handshakeResponse = null;
                    Exception error = null;
                    try {
                        handshakeResponse = getRawRequest().handshake();
                    } catch (Exception e) {
                        error = e;
                    }

                    if (error != null || handshakeResponse.getError() != null) {
                        // if there's a cached version return it before throwing the error
                        if (handshakeResponseCached != null && handshakeResponseCached.getError() == null) {
                            subscriber.onNext(handshakeResponseCached);
                        }

                        if (handshakeResponse!=null && handshakeResponse.getError() != null) {
                            throw new AmpacheApiException(handshakeResponse.getError());
                        } else if (error != null) {
                            throw new AmpacheApiException(error);
                        } else {
                            throw new Exception("undefined error");
                        }
                    }

                    if (checkAndCache(FILENAME_HANDSHAKE, handshakeResponse, handshakeResponseCached)) {
                        AmpacheSession.INSTANCE.setHandshakeResponse(handshakeResponse);
                        subscriber.onNext(handshakeResponse);
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .retry(4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the artists
     */
    public Observable<List<Artist>> getArtists() {
        return Observable.create(new OnSubscribe<List<Artist>>() {
            @Override
            public void call(final Subscriber<? super List<Artist>> subscriber) {
                AmpacheApi.this.call(subscriber, FILENAME_ARTISTS, ArtistsResponse.class, new Callable<ArtistsResponse>() {
                    @Override
                    public ArtistsResponse call() throws Exception {
                        return getRawRequest().getArtists(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    }
                });
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the artists
     */
    public Observable<Tags> getTags() {
        return Observable.create(new OnSubscribe<Tags>() {
            @Override
            public void call(final Subscriber<? super Tags> subscriber) {
                try {
                    TagsResponse tagsResponse = getRawRequest().getTags(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    subscriber.onNext(tagsResponse.getTags());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the albums for given artist
     */
    public Observable<List<Album>> getAlbumsFromArtist(final String artistId) {
        return Observable.create(new OnSubscribe<List<Album>>() {

            @Override
            public void call(final Subscriber<? super List<Album>> subscriber) {

                AmpacheApi.this.call(subscriber, String.format(FILENAME_ALBUMS_FROM_ARTIST, artistId), AlbumsResponse.class, new Callable<AlbumsResponse>() {
                    @Override
                    public AlbumsResponse call() throws Exception {
                        return getRawRequest().getAlbumsFromArtist(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), artistId);
                    }
                });


//                try {
//                    AlbumsResponse albumsResponse =
//                            getRawRequest().getAlbumsFromArtist(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), artistId);
//                    if (albumsResponse.getError()!=null) throw new AmpacheApiException(albumsResponse.getError());
//                    subscriber.onNext(albumsResponse.getAlbums());
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the albums
     */
    public Observable<List<Album>> getAlbums() {
        return Observable.create(new OnSubscribe<List<Album>>() {

            @Override
            public void call(final Subscriber<? super List<Album>> subscriber) {

                AmpacheApi.this.call(subscriber, FILENAME_ALBUMS, AlbumsResponse.class, new Callable<AlbumsResponse>() {
                    @Override
                    public AlbumsResponse call() throws Exception {
                        return getRawRequest().getAlbums(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    }
                });


//                try {
//                    AlbumsResponse albumsResponseCached = getCached(FILENAME_ALBUMS, AlbumsResponse.class);
//                    if (albumsResponseCached != null && albumsResponseCached.getError() == null) {
//                        subscriber.onNext(albumsResponseCached.getAlbums());
//                    }
//
//                    AlbumsResponse albumsResponse = getRawRequest().getAlbums(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
//                    if (albumsResponse.getError() != null) throw new AmpacheApiException(albumsResponse.getError());
//
//                    if (checkAndCache(FILENAME_ALBUMS, albumsResponse, albumsResponseCached)) {
//                        subscriber.onNext(albumsResponse.getAlbums());
//                    }
//
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get the album from the album id
     */
    public Observable<Album> getAlbumFromId(String albumId) {
        return Observable.create(new OnSubscribe<Album>() {

            @Override
            public void call(final Subscriber<? super Album> subscriber) {
                try {
                    AlbumsResponse albumResponse = getRawRequest().getAlbumFromId(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), albumId);
                    if (albumResponse.getError()!=null) throw new AmpacheApiException(albumResponse.getError());
                    subscriber.onNext(albumResponse.getAlbums().get(0));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get artist from artist id
     */
    public Observable<Artist> getArtistFromId(String artistId) {
        return Observable.create(new OnSubscribe<Artist>() {

            @Override
            public void call(final Subscriber<? super Artist> subscriber) {
                try {
                    ArtistsResponse artistsResponse = getRawRequest().getArtistFromId(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), artistId);
                    if (artistsResponse.getError()!=null) throw new AmpacheApiException(artistsResponse.getError());
                    subscriber.onNext(artistsResponse.getArtists().get(0));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the songs
     */
    public Observable<List<Song>> getSongs() {
        return Observable.create(new OnSubscribe<List<Song>>() {

            @Override
            public void call(final Subscriber<? super List<Song>> subscriber) {
                AmpacheApi.this.call(subscriber, FILENAME_SONGS, SongsResponse.class, new Callable<SongsResponse>() {
                    @Override
                    public SongsResponse call() throws Exception {
                        return getRawRequest().getSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    }
                });


//                try {
//                    SongsResponse songsResponseCached = getCached(FILENAME_SONGS,SongsResponse.class);
//                    if (songsResponseCached!=null && songsResponseCached.getError()==null) {
//                        subscriber.onNext(songsResponseCached.getSongs());
//                    }
//
//                    SongsResponse songsResponse = getRawRequest().getSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
//                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());
//
//                    if(checkAndCache(FILENAME_SONGS,songsResponse,songsResponseCached)){
//                        subscriber.onNext(songsResponse.getSongs());
//                    }
//
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(18)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of all the songs that match the filter
     */
    public Observable<List<Song>> searchSongs(final String filter) {
        return Observable.create(new OnSubscribe<List<Song>>() {

            @Override
            public void call(final Subscriber<? super List<Song>> subscriber) {
                try {
                    SongsResponse songsResponse = getRawRequest().searchSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), filter);
                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());
                    subscriber.onNext(songsResponse.getSongs());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(18)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of songs from given album
     */
    public Observable<List<Song>> getSongsFromAlbum(final String albumId) {
        return Observable.create(new OnSubscribe<List<Song>>() {

            @Override
            public void call(final Subscriber<? super List<Song>> subscriber) {
                AmpacheApi.this.call(subscriber, String.format(FILENAME_SONGS_FROM_ALBUM, albumId), SongsResponse.class, new Callable<SongsResponse>() {
                    @Override
                    public SongsResponse call() throws Exception {
                        return getRawRequest().getSongsFromAlbum(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), albumId);
                    }
                });


//                try {
//                    SongsResponse songsResponseCached = getCached(FILENAME_SONGS_FROM_ALBUM, SongsResponse.class);
//                    if (songsResponseCached != null && songsResponseCached.getError() == null) {
//                        subscriber.onNext(songsResponseCached.getSongs());
//                    }
//
//                    SongsResponse songsResponse =
//                            getRawRequest().getSongsFromAlbum(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), albumId);
//                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());
//
//                    if (checkAndCache(FILENAME_SONGS_FROM_ALBUM, songsResponse, songsResponseCached)) {
//                        subscriber.onNext(songsResponse.getSongs());
//                    }
//
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of songs from given album
     */
    public Observable<List<Playlist>> getPlaylists() {
        return Observable.create(new OnSubscribe<List<Playlist>>() {

            @Override
            public void call(final Subscriber<? super List<Playlist>> subscriber) {
                AmpacheApi.this.call(subscriber, FILENAME_PLAYLISTS, PlaylistsResponse.class, new Callable<PlaylistsResponse>() {
                    @Override
                    public PlaylistsResponse call() throws Exception {
                        return getRawRequest().getPlaylists(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    }
                });


//                try {
//                    PlaylistsResponse songssResponse =
//                            getRawRequest().getPlaylists(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
//                    if (songssResponse.getError()!=null) throw new AmpacheApiException(songssResponse.getError());
//                    subscriber.onNext(songssResponse.getPlaylists());
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of songs from given album
     */
    public Observable<List<Playlist>> getPlaylist(final String playlistId) {
        return Observable.create(new OnSubscribe<List<Playlist>>() {

            @Override
            public void call(final Subscriber<? super List<Playlist>> subscriber) {
                try {
                    PlaylistsResponse songssResponse =
                            getRawRequest().getPlaylist(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), playlistId);
                    if (songssResponse.getError()!=null) throw new AmpacheApiException(songssResponse.getError());
                    subscriber.onNext(songssResponse.getPlaylists());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of songs from given album
     */
    public Observable<List<Song>> getPlaylistSongs(final String playlistId) {
        return Observable.create(new OnSubscribe<List<Song>>() {

            @Override
            public void call(final Subscriber<? super List<Song>> subscriber) {
                AmpacheApi.this.call(subscriber, String.format(FILENAME_PLAYLIST_SONGS, playlistId), SongsResponse.class, new Callable<SongsResponse>() {
                    @Override
                    public SongsResponse call() throws Exception {
                        return getRawRequest().getPlaylistSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), playlistId);
                    }
                });


//                try {
//                    SongsResponse songsResponse =
//                            getRawRequest().getPlaylistSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), playlistId);
//                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());
//                    subscriber.onNext(songsResponse.getSongs());
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get a list of songs from given album
     */
    public Observable<List<Song>> createPlaylist(final String name) {
        return Observable.create(new OnSubscribe<List<Song>>() {

            @Override
            public void call(final Subscriber<? super List<Song>> subscriber) {
                try {
                    SongsResponse songsResponse =
                            getRawRequest().createPlaylist(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), name, PlaylistType.PUBLIC);
                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());
                    subscriber.onNext(songsResponse.getSongs());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(9)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ping the server to stay logged in
     */
    public Observable<PingResponse> ping() {
        return Observable.create(new OnSubscribe<PingResponse>() {

            @Override
            public void call(final Subscriber<? super PingResponse> subscriber) {
                try {
                    PingResponse pingResponse = getRawRequest().ping(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    HandshakeResponse handshakeResponse = AmpacheSession.INSTANCE.getHandshakeResponse();
                    handshakeResponse.setSession_expire(pingResponse.getSession_expire());
                    AmpacheSession.INSTANCE.setHandshakeResponse(handshakeResponse);
                    subscriber.onNext(pingResponse);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .doOnError(doOnError)
                .retry(22)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void resetCredentials() {
        AmpacheSession.INSTANCE.setAmpachePassword(null);
        AmpacheSession.INSTANCE.setAmpacheUrl(null);
        AmpacheSession.INSTANCE.setAmpacheUser(null);
    }

    /**
     * get saved response
     * @param filename  name of the saved file
     * @param tClass    .class of the object to return
     * @return          the saved file
     */
    private <T extends BaseResponse> T getCached(String filename, Class<T> tClass) {
        if (filename == null) return null;
        // return the cached first
        String cachedJson = FileUtil.getInstance().readStringFile(mContext,filename);
        T songsResponseCached = new Gson().fromJson(cachedJson,tClass);
        return songsResponseCached;
    }

    /**
     *
     * @param filename
     * @param response
     * @param cachedResponse
     * @return  true if the response and the cached response are not equals,
     *          thus true the response has changed.
     */
    private boolean checkAndCache(String filename, BaseResponse response, BaseResponse cachedResponse) {
        // if nothing is cached cache the response for the first time
        // if the cached response and the new response are different cache the new one
        if(cachedResponse==null || !cachedResponse.equals(response)) {
            // cache the new song response
            try {
                FileUtil.getInstance().writeStringFile(mContext,filename,response.toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void cleanupFiles() {
        // TODO remove created files on log out
    }

    Action1<Throwable> doOnError = new Action1<Throwable>() {

        @Override
        public void call(final Throwable throwable) {
            try {
                if ((throwable instanceof AmpacheApiException)) {
                    int code = Integer.parseInt(((AmpacheApiException) throwable).getAmpacheError().getCode());
                    if (code >= 400) {
                        getRawRequest().handshake();
                    }
                }
            }catch (Exception e){
                try {
                    getRawRequest().handshake();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    };
}
