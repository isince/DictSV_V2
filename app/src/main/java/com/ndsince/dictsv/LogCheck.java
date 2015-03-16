package com.ndsince.dictsv;

import android.util.Log;

/**
 * Log check
 */
public class LogCheck {

    /**
     * Debug Log -
     * [TAG] method : message
     * @param TAG Tag class name
     * @param method neame of method
     * @param message text log message
     */
    public static void d(String TAG, String method, String message) {
        Log.d("LOG", "[" + TAG + "] " + method + " : " + message);
    }

    /**
     * Info Log -
     * [TAG] method : message
     * @param TAG Tag class name
     * @param method neame of method
     * @param message text log message
     */
    public static void i(String TAG, String method, String message) {
        Log.i("LOG", "[" + TAG + "] " + method + " : " + message);
    }

    /**
     * Info Log -
     * [TAG] method : message
     * @param TAG Tag class name
     * @param method neame of method
     * @param message text log message
     */
    public static void e(String TAG, String method, String message) {
        Log.e("LOG", "[" + TAG + "] " + method + " : " + message);
    }
}
