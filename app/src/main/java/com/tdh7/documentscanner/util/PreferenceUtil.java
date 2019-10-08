package com.tdh7.documentscanner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.ui.widget.CaptureView;

public final class PreferenceUtil {
    public static final String SAVED_CAPTURE_MODE = "saved_capture_mode";
    private static PreferenceUtil sInstance;
    private final SharedPreferences mPreferences;
    private PreferenceUtil(@NonNull final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(App.getInstance().getApplicationContext());
        }
        return sInstance;
    }

    public int getSavedCaptureMode() {
        return mPreferences.getInt(SAVED_CAPTURE_MODE, CaptureView.MODE_MANUAL_CAPTURE);
    }

    public void setSavedOriginal3DPhoto(int mode) {
        mPreferences.edit().putInt(SAVED_CAPTURE_MODE,mode).apply();
    }


}