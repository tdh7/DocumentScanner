package com.tdh7.documentscanner.ui.widget.custom;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ThemeUpdater {
    public static ThemeUpdater sInstance;
    public ThemeUpdater getInstance() {
        if(sInstance==null) sInstance = new ThemeUpdater();
        return sInstance;
    }

    public void Destroy() {
        sInstance = null;
    }
}
