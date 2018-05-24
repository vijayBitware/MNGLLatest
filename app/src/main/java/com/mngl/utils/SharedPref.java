package com.mngl.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class for Store data to locally in Shared Preferences.
 *
 * @author (Arun Chougule)
 */
public class SharedPref {

    private static Context context;
    public static final String PREF_NAME = "mngl_preference";
    public static final String GCMREGID = "gcmregid";
    public static String LOG_IN = "login";
    public static final String USER_TOKEN = "token";
    public static final String USER_FIRSTNAME="user_firstname";
    public static final String USER_LASTNAME="user_lastname";
    public static final String USER_MOBILE = "user_mobile";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_IMAGE = "profile_image";
    public static final String USER_ID = "uer_id";
    public static final String USER_STATUS = "user_status";



    public SharedPref(Context c) {
        context = c;
    }

    public static SharedPreferences getPreferences() {

        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
    }

    public static void writeString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public static void getString(String key, String value) {
        SharedPref.getPreferences().getString(key, value);
    }

    public static Editor getEditor() {

        return getPreferences().edit();
    }

    public static boolean getBoolean(String pREF_KEY_TWITTER_LOGIN, boolean b) {
        // TODO Auto-generated method stub
        return getEditor().putBoolean(pREF_KEY_TWITTER_LOGIN, b).commit();
    }

}
