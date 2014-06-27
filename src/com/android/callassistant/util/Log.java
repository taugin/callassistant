package com.android.callassistant.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static final String TAG = "taugin";
    private static final boolean DEBUG = true;

    private static Log sLog;
    private Context mContext;
    public static Log getLog(Context context) {
        if (sLog == null) {
            sLog = new Log(context);
        }
        return sLog;
    }
    private Log(Context context) {
        mContext = context;
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (DEBUG) {
            android.util.Log.v(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (DEBUG) {
            android.util.Log.i(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            android.util.Log.e(tag, message);
        }
    }

    public void recordOperation(String operation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = sdf.format(new Date(System.currentTimeMillis())) + " : ";
        d("taugin1", time + operation);
        //Runtime.getRuntime().exec("cat " + operation + " > ")
    }
}
