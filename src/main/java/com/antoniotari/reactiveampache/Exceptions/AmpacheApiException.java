package com.antoniotari.reactiveampache.Exceptions;

import java.util.List;

import com.antoniotari.reactiveampache.models.AmpacheModel;
import com.antoniotari.reactiveampache.models.Error;

/**
 * Created by antoniotari on 2016-05-21.
 */
public class AmpacheApiException extends Exception {

    public final static String CODE_UNDEFINED = "999";
    public final static String ERROR_UNDEFINED = "TODO";

    private final Error mAmpacheError;
    private List<? extends AmpacheModel> cachedAmpacheModels;

    public AmpacheApiException(final Error ampacheError) {
        super("code: " + ampacheError.getCode() + " --- error: " + ampacheError.getError());
        mAmpacheError = ampacheError;
    }

    public AmpacheApiException(final Throwable throwable) {
        super(throwable);
        mAmpacheError = new Error(CODE_UNDEFINED, throwable);
    }

    public AmpacheApiException(final String code, final String message) {
        super("code: " + code + " --- error: " + message);
        mAmpacheError = new Error(code, message);
    }

    public AmpacheApiException(final Error ampacheError, List<? extends AmpacheModel> cachedAmpacheModels) {
        this(ampacheError);
        this.cachedAmpacheModels = cachedAmpacheModels;
    }

    public Error getAmpacheError() {
        return mAmpacheError;
    }

    public List<? extends AmpacheModel> getCachedAmpacheModels() {
        return cachedAmpacheModels;
    }

    public boolean isUndefinedCode() {
        return mAmpacheError.getCode().equalsIgnoreCase(AmpacheApiException.CODE_UNDEFINED);
    }

    public boolean isUndefinedErrorMessage() {
        return mAmpacheError.getError().equalsIgnoreCase(ERROR_UNDEFINED);
    }
}
