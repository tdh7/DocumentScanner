package com.tdh7.documentscanner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.tdh7.documentscanner.App;

public final class PreferenceUtil {
    private static PreferenceUtil sInstance;
    private final SharedPreferences mSharedPreferences;
    private PreferenceUtil(@NonNull final Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(App.getInstance().getApplicationContext());
        }
        return sInstance;
    }

}