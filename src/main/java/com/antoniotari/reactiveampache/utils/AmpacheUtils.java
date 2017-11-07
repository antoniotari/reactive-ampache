package com.antoniotari.reactiveampache.utils;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        String year1 = null;
                        String year2 = null;
                        if (o1 == null || o1.getSortYear() == null) year1 = "0";
                        if (o2 == null || o2.getSortYear() == null) year2 = "0";
                        if (year1 == null) year1 = o1.getSortYear();
                        if (year2 == null) year2 = o2.getSortYear();
                        return year1.compareTo(year2);
                    case TAG:
                        String tag1 = null;
                        String tag2 = null;
                        if (o1 == null || o1.getSortTag() == null) tag1 = "0";
                        if (o2 == null || o2.getSortTag() == null) tag2 = "0";
                        if (tag1 == null) tag1 = o1.getSortTag();
                        if (tag2 == null) tag2 = o2.getSortTag();
                        // tag might be track number
                        if(isNumber(tag1) && isNumber(tag2)) {
                            return Integer.parseInt(tag1) - Integer.parseInt(tag2);
                        } else {
                            return tag1.compareTo(tag2);
                        }
                    case NAME:
                    default:
                        String name1 = null;
                        String name2 = null;
                        if (o1 == null || o1.getSortName() == null) name1 = "";
                        if (o2 == null || o2.getSortName() == null) name2 = "";
                        if (name1 == null) name1 = o1.getSortName();
                        if (name2 == null) name2 = o2.getSortName();
                        return name1.compareTo(name2);
                }
            }
        });
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("^-?\\d+\\.?\\d*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
