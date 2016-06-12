package com.antoniotari.reactiveampache.utils;

import android.content.Context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public final class MD5 {
    private static char[] hextable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    //store the known MD5 in a cache
    static HashMap<String, String> _md5Cache = null;
    static HashMap<String, Integer> _md5IntCache = null;

    private static String getMD5FromCache(String key) {
        if (_md5Cache == null) {
            return null;
        }
        return _md5Cache.get(key);
    }

    private static Integer getMD5IntFromCache(String key) {
        if (_md5IntCache == null) {
            return null;
        }
        return _md5IntCache.get(key);
    }

    private static void putMD5Cache(String key, String value) {
        if (key == null || value == null) {
            return;
        }

        if (_md5Cache == null) {
            _md5Cache = new HashMap<String, String>();
        }
        _md5Cache.put(key, value);
    }

    private static void putMD5IntCache(String key, Integer value) {
        if (key == null || value == null) {
            return;
        }

        if (_md5IntCache == null) {
            _md5IntCache = new HashMap<String, Integer>();
        }
        _md5IntCache.put(key, value);
    }

    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------

    public static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
        }
        return s;
    }

    //--------------------------------------------------------------------------------

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        m.update(s.getBytes(), 0, s.length());
        return byteArrayToHex(m.digest());
    }

    //--------------------------------------------------------------------------------
    public static String md5(String s) {
        if (s == null) {
            return "";
        }

        String retS = getMD5FromCache(s);
        if (retS == null) {
            retS = digest(s, "MD5");
            putMD5Cache(s, retS);
        }
//        else{
//        	Log.hi("md5 , getting from cache",retS);
//        }

        return retS;
    }

    //--------------------------------------------------------------------------------
    public static int md5Int(String s) {
        String md5S = md5(s);
        Integer retS = getMD5IntFromCache(md5S);
        if (retS == null) {
            char[] charr = md5S.toCharArray();
            //String resultString="";
            int total = 0;
            for (int i = 0; i < charr.length; i++) {
                int num = charr[i];

                total += num * (BigInteger.valueOf(10).pow(i)).intValue();

//                if((i+1)%8==0){
//                    //resultString=resultString+total+"";
//                    total=0;
//                }
            }
            retS = total;
            putMD5IntCache(md5S, retS);
//            try{
//                retS=(Long.parseLong(resultString)).intValue();
//                putMD5IntCache(md5S,retS);
//            }catch(NumberFormatException e){}

        }
//        else{
//        	Log.hi("md5Int , getting from cache",retS);
//        }
        return retS;

//
//		char[] charr= digest(s,"MD5").toCharArray();
//		int total=Integer.MIN_VALUE;
//		for(int i=0;i<charr.length;i++)
//		{
//			int num=charr[i];
//			total+=num;
//		}
//		return total;
    }

    public static final String MD5_FILENAME = "md5stored";
    public static final String MD5INT_FILENAME = "md5Intstored";

    public static void storeMD5s(Context context) {
        if (_md5Cache != null) {
            FileUtil.getInstance().storeSerializable(context, MD5_FILENAME, _md5Cache);
        }

        if (_md5IntCache != null) {
            FileUtil.getInstance().storeSerializable(context, MD5INT_FILENAME, _md5IntCache);
        }
    }

    private static boolean checkExists() {
        return FileUtil.fileExistsAndCanRead(MD5_FILENAME);
        //File f = new File(MD5_FILENAME);
        //return(f.exists() && !f.isDirectory() && f.canRead());
    }

    public static HashMap<String, String> readMD5s(Context context) {
        try {
            if (checkExists()) {
                _md5Cache = (HashMap<String, String>) FileUtil.getInstance().readSerializable(context, MD5_FILENAME);
                if (_md5Cache == null) {
                    _md5Cache = new HashMap<String, String>();
                }
                return _md5Cache;
            }
        } catch (Exception e1) {
            Log.error(e1);
        }
        return null;
    }

    public static HashMap<String, Integer> readMD5Ints(Context context) {
        try {
            if (checkExists()) {
                _md5IntCache = (HashMap<String, Integer>) FileUtil.getInstance().readSerializable(context, MD5INT_FILENAME);
                if (_md5IntCache == null) {
                    _md5IntCache = new HashMap<String, Integer>();
                }
                return _md5IntCache;
            }
        } catch (Exception e1) {
            Log.error(e1);
        }
        return null;
    }
}