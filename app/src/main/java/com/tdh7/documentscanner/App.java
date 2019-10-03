package com.tdh7.documentscanner;

import android.app.Application;

import com.tdh7.documentscanner.util.PreferenceUtil;
import com.tdh7.documentscanner.util.Tool;

public class App extends Application {
    private static App mInstance;

    public static synchronized App getInstance() {
        return mInstance;
    }

    public PreferenceUtil getPreferencesUtility() {
        return PreferenceUtil.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Tool.init(this);
    }
}