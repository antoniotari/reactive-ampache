package com.antoniotari.reactiveampache.utils;

import java.io.Reader;
import java.io.StringReader;

import com.google.gson.Gson;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by antonio.tari on 5/19/16.
 */
public class SerializeUtils {

    public <T> T fromXml(String xmlString,Class<T> classOfT) throws Exception {
        Serializer serializer = new Persister();
        Reader reader = new StringReader(xmlString);
        return serializer.read(classOfT, reader, false);
    }

    public <T> T fromJson(String jsonString,Class<T> classOfT) {
        return new Gson().fromJson(jsonString,classOfT);
    }

    public String toJsonString(Object object) {
        return new Gson().toJson(object);
    }
}
