package com.antoniotari.reactiveampache.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.antoniotari.reactiveampache.models.HandshakeResponse;
import com.antoniotari.reactiveampache.utils.SerializeUtils;

/**
 * Created by antonio.tari on 5/12/16.
 * used internally to keep the session
 * use AmpacheApi to initialize
 */
public enum AmpacheSession {
    INSTANCE;

    public static final String KEY_SHARED_PREFERENCES = "com.antoniotari.ampache.library";
    private static final String FILENAME_HANDSHAKE_RESPONSE = "com.antoniotari.ampache.library.handshake";
    private static final String FILENAME_USER = "com.antoniotari.ampache.library.user";
    private static final String FILENAME_URL = "com.antoniotari.ampache.library.url";
    private static final String FILENAME_PASSWORD = "com.antoniotari.ampache.library.password";

    private SharedPreferences mSharedPreferences;
    private HandshakeResponse mHandshakeResponse;
    private String mAmpacheUrl;
    private String mAmpacheUser;
    private String mAmpachePassword;

    void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public boolean isUserAuthenticated() {
        return (getHandshakeResponse() != null && getAmpachePassword() != null && getAmpacheUrl() != null && getAmpacheUser() != null)
                && getHandshakeResponse().getAuth() != null;
    }

    void setAmpacheUrl(final String ampacheUrl) {
        mAmpacheUrl = ampacheUrl;
        mSharedPreferences.edit()
                .putString(FILENAME_URL, ampacheUrl).apply();
    }

    void setAmpachePassword(final String ampachePassword) {
        mAmpachePassword = ampachePassword;
        mSharedPreferences.edit()
                .putString(FILENAME_PASSWORD, ampachePassword).apply();
    }

    void setAmpacheUser(final String ampacheUser) {
        mAmpacheUser = ampacheUser;
        mSharedPreferences.edit()
                .putString(FILENAME_USER, ampacheUser).apply();
    }

    public String getAmpachePassword() {
        if (mAmpachePassword == null) {
            mAmpachePassword = mSharedPreferences.getString(FILENAME_PASSWORD,null);
        }
        return mAmpachePassword;
    }

    public String getAmpacheUrl() {
        if (mAmpacheUrl == null) {
            mAmpacheUrl = mSharedPreferences.getString(FILENAME_URL,null);
        }
        return mAmpacheUrl;
    }

    public String getAmpacheUser() {
        if (mAmpacheUser == null) {
            mAmpacheUser = mSharedPreferences.getString(FILENAME_USER,null);
        }
        return mAmpacheUser;
    }

    void setHandshakeResponse(final HandshakeResponse handshakeResponse) {
        mHandshakeResponse = handshakeResponse;
        writeHandshakeResponseToFile(handshakeResponse);
    }

    public HandshakeResponse getHandshakeResponse() {
        if (mHandshakeResponse == null) {
            mHandshakeResponse = readHandshakeResponseFromFile();
        }
        return mHandshakeResponse;
    }

    private void writeHandshakeResponseToFile(final HandshakeResponse handshakeResponse) {
        String handshakeString = new SerializeUtils().toJsonString(handshakeResponse);
        // check if the file already exists
        String saved = readHandshakeResponseStringFromFile();
        if (saved == null || !saved.equals(handshakeString)) {
            mSharedPreferences.edit()
                    .putString(FILENAME_HANDSHAKE_RESPONSE, handshakeString).commit();
        }
    }

    private String readHandshakeResponseStringFromFile() {
        return mSharedPreferences.getString(FILENAME_HANDSHAKE_RESPONSE, null);
    }

    private HandshakeResponse readHandshakeResponseFromFile() {
        HandshakeResponse handshakeResponse = null;
        String handshakeString = readHandshakeResponseStringFromFile();
        if (handshakeString != null) {
            handshakeResponse = new SerializeUtils().fromJson(handshakeString, HandshakeResponse.class);
        }
        return handshakeResponse;
    }

    public void clearToken() {
        mSharedPreferences.edit()
                .putString(FILENAME_HANDSHAKE_RESPONSE, "").commit();
    }
}
