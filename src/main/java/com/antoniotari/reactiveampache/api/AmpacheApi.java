package com.antoniotari.reactiveampache.api;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import com.antoniotari.reactiveampache.Exceptions.AmpacheApiException;
import com.antoniotari.reactiveampache.models.Album;
import com.antoniotari.reactiveampache.models.AlbumsResponse;
import com.antoniotari.reactiveampache.models.Artist;
import com.antoniotari.reactiveampache.models.ArtistsResponse;
import com.antoniotari.reactiveampache.models.BaseResponse;
import com.antoniotari.reactiveampache.models.HandshakeResponse;
import com.antoniotari.reactiveampache.models.PingResponse;
import com.antoniotari.reactiveampache.models.Song;
import com.antoniotari.reactiveampache.models.SongsResponse;
import com.antoniotari.reactiveampache.utils.FileUtil;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by antonio tari on 2016-05-21.
 */
public enum AmpacheApi {
    INSTANCE;

    private static final String FILENAME_ARTISTS = "com.antoniotari.ampache.library.response.artists.json";
    private static final String FILENAME_ALBUMS = "com.antoniotari.ampache.library.response.albums.json";
    private static final String FILENAME_SONGS = "com.antoniotari.ampache.library.response.songs.json";

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
    public Observable<Void> initUser(final String ampacheUrl, String ampacheUser, String ampachePassword) {
        return Observable.create(new OnSubscribe<Void>() {

            @Override
            public void call(final Subscriber<? super Void> subscriber) {
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
                    subscriber.onNext(null);
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
    public Observable<Void> initUser() {
        return initUser(AmpacheSession.INSTANCE.getAmpacheUrl(),
                AmpacheSession.INSTANCE.getAmpacheUser(),
                AmpacheSession.INSTANCE.getAmpachePassword());
    }

    /**
     * before making any API call must handshake with the server
     */
    public Observable<HandshakeResponse> handshake() {
        return Observable.create(new OnSubscribe<HandshakeResponse>() {

            @Override
            public void call(final Subscriber<? super HandshakeResponse> subscriber) {
                try {
                    HandshakeResponse handshakeResponse = mRawRequest.handshake();

                    if (handshakeResponse.getError() != null) throw new AmpacheApiException(handshakeResponse.getError());

                    AmpacheSession.INSTANCE.setHandshakeResponse(handshakeResponse);
                    subscriber.onNext(handshakeResponse);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .retry(9)
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
                try {
                    ArtistsResponse artistsResponseCached = getCached(FILENAME_ARTISTS, ArtistsResponse.class);
                    if (artistsResponseCached != null && artistsResponseCached.getError() == null &&
                            artistsResponseCached.getArtists() != null) {
                        subscriber.onNext(artistsResponseCached.getArtists());
                    }

                    ArtistsResponse artistsResponse = mRawRequest.getArtists(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    if (artistsResponse.getError() != null) throw new AmpacheApiException(artistsResponse.getError());

                    if (checkAndCache(FILENAME_ARTISTS, artistsResponse, artistsResponseCached)) {
                        subscriber.onNext(artistsResponse.getArtists());
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
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
                try {
                    AlbumsResponse albumsResponse =
                            mRawRequest.getAlbumsFromArtist(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), artistId);
                    if (albumsResponse.getError()!=null) throw new AmpacheApiException(albumsResponse.getError());
                    subscriber.onNext(albumsResponse.getAlbums());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
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
                try {
                    AlbumsResponse albumsResponseCached = getCached(FILENAME_ALBUMS, AlbumsResponse.class);
                    if (albumsResponseCached != null && albumsResponseCached.getError() == null) {
                        subscriber.onNext(albumsResponseCached.getAlbums());
                    }

                    AlbumsResponse albumsResponse = mRawRequest.getAlbums(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    if (albumsResponse.getError() != null) throw new AmpacheApiException(albumsResponse.getError());

                    if (checkAndCache(FILENAME_ALBUMS, albumsResponse, albumsResponseCached)) {
                        subscriber.onNext(albumsResponse.getAlbums());
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
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
                try {
                    SongsResponse songsResponseCached = getCached(FILENAME_SONGS,SongsResponse.class);
                    if (songsResponseCached!=null && songsResponseCached.getError()==null) {
                        subscriber.onNext(songsResponseCached.getSongs());
                    }

                    SongsResponse songsResponse = mRawRequest.getSongs(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
                    if (songsResponse.getError()!=null) throw new AmpacheApiException(songsResponse.getError());

                    if(checkAndCache(FILENAME_SONGS,songsResponse,songsResponseCached)){
                        subscriber.onNext(songsResponse.getSongs());
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
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
                try {
                    SongsResponse songssResponse =
                            mRawRequest.getSongsFromAlbum(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth(), albumId);
                    if (songssResponse.getError()!=null) throw new AmpacheApiException(songssResponse.getError());
                    subscriber.onNext(songssResponse.getSongs());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
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
                    PingResponse pingResponse = mRawRequest.ping(AmpacheSession.INSTANCE.getHandshakeResponse().getAuth());
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
    private <T extends BaseResponse> T getCached(String filename, Class<T> tClass){
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
}
