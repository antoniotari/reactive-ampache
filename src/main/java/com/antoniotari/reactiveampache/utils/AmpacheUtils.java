package com.antoniotari.reactiveampache.utils;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.antoniotari.reactiveampache.models.Sortable;
import com.antoniotari.reactiveampache.models.Sortable.SortOption;

/**
 * Created by antonio.tari on 5/12/16.
 */
public class AmpacheUtils {
    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static <T  extends Sortable> void sort(final List<T> sortableList, final SortOption sortoption) {
        if(sortableList == null) return;
        Collections.sort(sortableList, new Comparator<T>() {
            @Override
            public int compare(final T o1, final T o2) {
                switch (sortoption) {
                    case YEAR:
                        return o1.getSortYear().compareTo(o2.getSortYear());
                    case TAG:
                        return o1.getSortTag().compareTo(o2.getSortTag());
                    case NAME:
                    default:
                        return o1.getSortName().compareTo(o2.getSortName());
                }
            }
        });
    }
}
