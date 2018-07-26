package com.example.spoorthi.timerassignment.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalSharedPreferences
{
    private static LocalSharedPreferences localSharedPreferences;

    private SharedPreferences timerSharedPref;

    private String prefKey = "TIMER_SHARED_PREF";

    public static LocalSharedPreferences getInstance(Context context) {
        if (localSharedPreferences == null) {
            localSharedPreferences = new LocalSharedPreferences(context);
        }
        return localSharedPreferences;
    }

    private LocalSharedPreferences(Context context) {
        timerSharedPref = context.getSharedPreferences(prefKey,Context.MODE_PRIVATE);
    }

    public void savePauseData(String key,long value) {
        SharedPreferences.Editor prefsEditor = timerSharedPref.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    public long getPauseData(String key) {
        if (timerSharedPref!= null) {
            return timerSharedPref.getLong(key, 0);
        }
        return 0;
    }

    public void setTimerPaused(String key,boolean value) {
        SharedPreferences.Editor prefsEditor = timerSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public boolean isTimerPaused(String key) {
        if (timerSharedPref!= null) {
            return timerSharedPref.getBoolean(key, false);
        }
        return false;
    }
}
