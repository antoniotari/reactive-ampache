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
        // remove all characters before the beginning of the xml string
        // some ampache servers send 2 bytes before the actual xml
        String firstLetter = String.valueOf(xmlString.charAt(0));
        while (!firstLetter.equals("<")) {
            xmlString = xmlString.substring(1);
            firstLetter = String.valueOf(xmlString.charAt(0));
        }

        String lastLetter = String.valueOf(xmlString.charAt(xmlString.length()-1));
        while (!lastLetter.equals(">")) {
            xmlString = xmlString.substring(0,xmlString.length()-1);
            lastLetter = String.valueOf(xmlString.charAt(xmlString.length()-1));
        }

        // simple parser will fail when it finds & surrounded by spaces
        xmlString = xmlString.replace(" & "," &amp; ");
//        xmlString = xmlString.replace("?","&#63;");

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
