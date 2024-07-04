package com.example.dreaminterpreterai;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "DreamInterpreterPrefs";
    private static final String LAST_SESSION_TIME = "lastSessionTime";

    public static void saveLastSessionTime(Context context, long timestamp) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_SESSION_TIME, timestamp);
        editor.apply();
    }

    public static long getLastSessionTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(LAST_SESSION_TIME, 0);
    }
}
