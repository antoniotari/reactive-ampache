package com.antoniotari.reactiveampache.api;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import com.antoniotari.reactiveampache.models.AlbumsResponse;
import com.antoniotari.reactiveampache.models.ArtistsResponse;
import com.antoniotari.reactiveampache.models.HandshakeResponse;
import com.antoniotari.reactiveampache.models.PingResponse;
import com.antoniotari.reactiveampache.models.SongsResponse;
import com.antoniotari.reactiveampache.utils.AmpacheUtils;
import com.antoniotari.reactiveampache.utils.SerializeUtils;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by antonio.tari on 5/12/16.
 */
public class RawRequest {

    private final String mAmpacheUrl;
    private final String mAmpacheUser;
    private final String mAmpachePassword;
    private static final String API_ENDPOINT = "server/xml.server.php?";

    private final OkHttpClient clientLongTimeout;
    private final OkHttpClient clientMediumTimeout;
    private final OkHttpClient clientShortTimeout;

    @IntDef ({Timeout.LONG_TIMEOUT, Timeout.MEDIUM_TIMEOUT, Timeout.SHORT_TIMEOUT})
    @Retention (RetentionPolicy.SOURCE)
    @interface Timeout {
        int LONG_TIMEOUT = 0;
        int MEDIUM_TIMEOUT = 1;
        int SHORT_TIMEOUT = 2;
    }


    public RawRequest(@NonNull final String ampacheUrl,@NonNull String ampacheUser,@NonNull String ampachePassword){
        mAmpachePassword = ampachePassword;
        mAmpacheUrl = ampacheUrl;
        mAmpacheUser = ampacheUser;

        Builder bLong = new Builder()
                .readTimeout(99, TimeUnit.SECONDS)
                .writeTimeout(99, TimeUnit.SECONDS)
                .connectTimeout(99, TimeUnit.SECONDS);
        clientLongTimeout = bLong.build();

        Builder bMedium = new Builder()
                .readTimeout(44, TimeUnit.SECONDS)
                .writeTimeout(44, TimeUnit.SECONDS)
                .connectTimeout(44, TimeUnit.SECONDS);
        clientMediumTimeout = bMedium.build();

        Builder bShort = new Builder()
                .readTimeout(11, TimeUnit.SECONDS)
                .writeTimeout(11, TimeUnit.SECONDS)
                .connectTimeout(11, TimeUnit.SECONDS);
        clientShortTimeout = bShort.build();
    }

    public String getRequest(final String query, @Timeout int timeout) throws IOException {
        Request request = new Request.Builder()
                .url(mAmpacheUrl + API_ENDPOINT + query)
                .build();

        OkHttpClient client;
        switch (timeout) {

            case Timeout.LONG_TIMEOUT:
                client = clientLongTimeout;
                break;
            case Timeout.MEDIUM_TIMEOUT:
                client = clientMediumTimeout;
                break;
            case Timeout.SHORT_TIMEOUT:
            default:
                client = clientShortTimeout;
                break;
        }

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        return response.body().string();
    }

    public HandshakeResponse handshake(final String user, final String password) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String key = AmpacheUtils.sha256(password);
        String passphrase = AmpacheUtils.sha256(timestamp + key);
        String handshakeQuery = "action=handshake&auth=" +
                passphrase + "&timestamp=" +
                timestamp + "&version=350001&user=" +
                user;

        final String respStr = getRequest(handshakeQuery, Timeout.SHORT_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, HandshakeResponse.class);
    }

    public HandshakeResponse handshake() throws Exception {
        return handshake(mAmpacheUser, mAmpachePassword);
    }

    public ArtistsResponse getArtists(final String auth) throws Exception {
        String artistQuery = "auth=" + auth +
                //"&offset=252" +
                //"&limit=100" +
                "&action=artists";
        final String respStr = getRequest(artistQuery, Timeout.MEDIUM_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, ArtistsResponse.class);
    }

    public AlbumsResponse getAlbumsFromArtist(final String auth, final String artistId) throws Exception {
        String artistQuery = "auth=" + auth +
                "&filter=" + artistId +
                //"&limit=100" +
                "&action=artist_albums";
        final String respStr = getRequest(artistQuery,Timeout.SHORT_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, AlbumsResponse.class);
    }

    public AlbumsResponse getAlbums(final String auth) throws Exception {
        String artistQuery = "auth=" + auth +
                //"&limit=100" +
                "&action=albums";

        final String respStr = getRequest(artistQuery, Timeout.LONG_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, AlbumsResponse.class);
    }

    public SongsResponse getSongsFromAlbum(final String auth, final String albumId) throws Exception {
        String artistQuery = "auth=" + auth +
                "&filter=" + albumId +
                //"&limit=100" +
                "&action=album_songs";
        final String respStr = getRequest(artistQuery,Timeout.SHORT_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, SongsResponse.class);
    }

    public SongsResponse getSongs(final String auth) throws Exception {
        String artistQuery = "auth=" + auth +
                //"&limit=100" +
                "&action=songs";
        final String respStr = getRequest(artistQuery, Timeout.LONG_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, SongsResponse.class);
    }

    public PingResponse ping(final String auth) throws Exception {
        String pingQuery = "auth=" + auth + "&action=ping" +"&version=350001";
        final String respStr = getRequest(pingQuery, Timeout.SHORT_TIMEOUT);
        return new SerializeUtils().fromXml(respStr, PingResponse.class);
    }

}
