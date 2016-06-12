package com.antoniotari.reactiveampache.Exceptions;

import com.antoniotari.reactiveampache.models.Error;

/**
 * Created by antoniotari on 2016-05-21.
 */
public class AmpacheApiException extends Exception {

    private final Error mAmpacheError;

    public AmpacheApiException(final Error ampacheError) {
        super("code: "+ampacheError.getCode()+" --- error: "+ampacheError.getError());
        mAmpacheError = ampacheError;
    }

    public Error getAmpacheError() {
        return mAmpacheError;
    }
}
