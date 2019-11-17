package com.tdh7.documentscanner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.tdh7.documentscanner.App;

import static com.tdh7.documentscanner.ui.picker.CameraPickerFragment.CAPTURE_MODE_MANUAL_CAPTURE;

public final class PreferenceUtil {
    public static final String SAVED_CAPTURE_MODE = "saved_capture_mode";
    public static final int TYPE_LIST_ONE_ROW = 0;
    public static final int TYPE_GRID_TWO_ROW = 1;
    public static final int TYPE_GRID_FOUR_ROW = 2;
    public static final String LIST_VIEW_TYPE = "list_view_type";
    public static final String APP_THEME_OPTION = "app_theme_option";
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
        return mPreferences.getInt(SAVED_CAPTURE_MODE, CAPTURE_MODE_MANUAL_CAPTURE);
    }

    public void setSavedCaptureMode(int mode) {
        mPreferences.edit().putInt(SAVED_CAPTURE_MODE,mode).apply();
    }
    public int getSavedListType() {
        return mPreferences.getInt(LIST_VIEW_TYPE, TYPE_LIST_ONE_ROW);
    }

    public void setSavedListType(int mode) {
        mPreferences.edit().putInt(LIST_VIEW_TYPE,mode).apply();
    }

    public int getAppThemeOption() {
        return mPreferences.getInt(APP_THEME_OPTION,0);
    }

    public void setAppThemeOption(int mode) {
        mPreferences.edit().putInt(APP_THEME_OPTION,mode).apply();
    }
}