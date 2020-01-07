package com.tdh7.documentscanner.controller;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.tdh7.documentscanner.R;

import java.util.ArrayList;

public class ThemeStyle {
    private ArrayList<Boolean> mStyleList = new ArrayList<>();
    private Activity mActivity;
    public static void init(Activity activity, boolean initLight) {
        sInstance = new ThemeStyle();
        sInstance.mActivity = activity;
        sInstance.push(initLight);
    }

    public static void destroy() {
        if(sInstance!=null)
        sInstance.mActivity = null;
        sInstance = null;
    }

    public static void pushTheme(boolean light) {
        if(sInstance!=null)
        sInstance.push(light);
    }

    public void push(boolean light) {
        mStyleList.add(light);
        setTheme(light);
    }

    public void pop() {
        if(!mStyleList.isEmpty()) {
            boolean old = mStyleList.remove(mStyleList.size()-1);
            if(!mStyleList.isEmpty()&&mStyleList.get(mStyleList.size()-1)!=old) setTheme(!old);
        }
    }

    public static void popTheme() {
        if(sInstance!=null)
            sInstance.pop();
    }

    private void setTheme(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&mActivity!=null) {

            if (!light) {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                mActivity.getWindow().setNavigationBarColor(mActivity.getResources().getColor(R.color.backColorDark));
            } else {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                mActivity.getWindow().setNavigationBarColor(Color.WHITE);
            }
        }

    }

    private static ThemeStyle sInstance;
}
