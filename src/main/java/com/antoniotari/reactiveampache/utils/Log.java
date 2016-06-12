package com.antoniotari.reactiveampache.utils;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;

public final class Log {

    enum LogLevel {
        ERROR(0),
        WARN(1),
        INFO(2),
        DEBUG(3),
        VERB(4);

        int level;

        LogLevel(final int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    static LogLevel LOG_LEVEL = LogLevel.VERB;
    public static boolean isDebug = true;

    public static final String tagPrefix = "jedi.log.";//(JediUtil.getPackageName() == null ? Log.class.getCanonicalName() : JediUtil.getPackageName());
    public static final String tagDebug = tagPrefix + ".Log.debug";
    public static final String tagHi = tagPrefix + ".Log.hipriority";
    public static final String tagHttp = tagPrefix + ".Log.http";
    public static final String tagError = tagPrefix + ".Log.error";

    public static final String SEPARATOR_DEFAULT = " *** ";

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    static {
        if ("google_sdk".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT)) {
            LOG_LEVEL = LogLevel.VERB;
        } else {
            LOG_LEVEL = LogLevel.INFO;
        }
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void debug(Object... messages) {
        d(tagDebug, messages);
    }

    public static void log(Object... messages) {
        debug(messages);
    }

    public static void blu(Object... messages) {
        d("blublu", messages);
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void hi(Object... messages) {
        d(tagHi, messages);
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void http(Object... messages) {
        d(tagHttp, messages);
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void error(Object... messages) {
        e(tagError, messages);
    }

    public static void loge(Object... messages) {
        error(messages);
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------

    /**
     * this one also sends an error report to the server
     */
    public static void error(Context context, Object... messages) {
        e(tagError, messages);
        //ATErrorLog.getInstance().sendErrorLog(context, ATUtil.getInstance(context).getAppName(), buildString(messages));
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void c(Class tag, Object messages) {
        w(tag.getCanonicalName(), messages);
    }

    /**
     *
     * @param e
     * @return
     */
    public static String stackTraceToString(Throwable e) {
        if (e == null) {
            return null;
        }

        String resultStr = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            resultStr = sw.toString(); // stack trace as a string
            pw.flush();
            pw.close();
            sw.flush();
            sw.close();
        } catch (IOException ioe) {
        }
        return resultStr;
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static String checkString(Object str) {
        try {
            if (str == null) {
                return ("__null__");
            } else if (str instanceof Iterable) {
                StringBuilder sb = new StringBuilder("[");
                Iterator<Object> flavoursIter = ((Iterable) str).iterator();
                if (flavoursIter.hasNext()) {
                    sb.append(checkString(flavoursIter.next()));
                }
                while (flavoursIter.hasNext()) {
                    sb.append(", ");
                    sb.append(checkString(flavoursIter.next()));
                }
                sb.append("]");
                return checkString(sb.toString());
                //return checkString(ToStringBuilder.reflectionToString(str, ToStringStyle.MULTI_LINE_STYLE));
            } else if (str instanceof Throwable) {
                //String thStr = ((Throwable)str).getLocalizedMessage();
                return checkString(stackTraceToString((Throwable) str));
            } else if (str instanceof Exception) {
                StringBuilder sb = new StringBuilder("EXCEPTION ON CLASS: ");
                sb.append(str.getClass().getSimpleName());
                sb.append(", Exception ");
                if (str instanceof JSONException) {
                    sb.append("JSONException");
                } else if (str instanceof IOException) {
                    sb.append("IOException");
                } else if (str instanceof NullPointerException) {
                    sb.append("NullPointerException");
                } else if (str instanceof FileNotFoundException) {
                    sb.append("FileNotFoundException");
                }

                sb.append(" : ");
                sb.append(checkString(((Exception) str).getLocalizedMessage()));
                sb.append(SEPARATOR_DEFAULT);
                sb.append(stackTraceToString((Exception) str));

                return checkString(sb.toString());
//				return checkString("EXCEPTION ON CLASS: "+str.getClass().getSimpleName()+", exception:")+
//						checkString(((Exception)str).getLocalizedMessage());
            } else if (
                    ((str instanceof String) && (
                            ((String) str).isEmpty() || ((String) str).equalsIgnoreCase(" "))
                    )
                            ||
                            ((str.toString().isEmpty() || str.toString().equalsIgnoreCase(" ")))
                    ) {
                return "__empty__";
            } else {
                return str.toString();
            }
        } catch (Exception d) {
            return "Cannot log, Exception: " + checkString(d.getLocalizedMessage());
        }
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static String buildString(Object[] sa) {
        return buildString(sa, SEPARATOR_DEFAULT);
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static String buildString(Object[] sa, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sa.length; i++) {
            sb.append(checkString(sa[i]));
            if (i < (sa.length - 1)) {
                sb.append(separator);
            }
        }

        String stackTrace = calcStackLine(Arrays.asList(Log.class.getName()));
        return stackTrace + "\t" + sb.toString();
    }

    /**
     * Error
     */
    public static void e(String tag, Object... string) {
        if (isDebug) {
            android.util.Log.e(tag, buildString(string));
        }
    }

    /**
     * Warn
     */
    public static void w(String tag, Object... string) {
        if (isDebug) {
            android.util.Log.w(tag, buildString(string));
        }
    }

    /**
     * Info
     */
    public static void i(String tag, Object... string) {
        if (isDebug) {
            if (LOG_LEVEL.ordinal() >= LogLevel.INFO.ordinal()) {
                android.util.Log.i(tag, buildString(string));
            }
        }
    }

    /**
     * Debug
     */
    public static void d(String tag, Object... string) {
        if (isDebug) {
            //if(LOG_LEVEL >= DEBUG)
            {
                android.util.Log.d(tag, buildString(string));
            }
        }
    }

    /**
     * Verbose
     */
    public static void v(String tag, Object... string) {
        if (isDebug) {
            //if(LOG_LEVEL >= VERB)
            {
                android.util.Log.v(tag, buildString(string));
            }
        }
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void Toast(Context c, Object... message) {
        if (isShowDebugMessages()) {
            Toast.makeText(c, buildString(message), Toast.LENGTH_LONG).show();
        }
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    public static void Toast(Context c, int stringId) {
        if (isShowDebugMessages()) {
            Toast.makeText(c, c.getResources().getString(stringId), Toast.LENGTH_LONG).show();
        }
    }

    //---------------------------------------------------------------------------------------
    //-----------------------------------
    static boolean isShowDebugMessages() {
        return true;
    }

    /**
     * @param ignoreClasses List of class names to ignore.
     */
    private static String calcStackLine(List<String> ignoreClasses) {

        StackTraceElement line = null;
        // Find the first line below an ignored class.
        for (StackTraceElement stack : new Throwable().getStackTrace()) {
            if (ignoreClasses.contains(stack.getClassName())) {
                line = null;
            } else if (line == null) {
                line = stack;
            }
        }
        if (line == null) {
            return "";
        }
        String desc = line.getClassName();
        int period = desc.lastIndexOf(".");
        if (period != -1) {
            desc = desc.substring(period + 1);
        }
        desc += "." + line.getMethodName();
        int lineNumber = line.getLineNumber();
        if (lineNumber >= 0) {
            // Note: getLineNumber returns a negative number if the line number is unknown.
            desc += "(" + lineNumber + ")";
        }
        return desc;
    }
}