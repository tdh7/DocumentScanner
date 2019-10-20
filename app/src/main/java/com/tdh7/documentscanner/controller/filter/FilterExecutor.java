package com.tdh7.documentscanner.controller.filter;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class FilterExecutor {
    private static final String TAG = "FilterExecutor";

    ArrayList<FilterResultCallback> mFilterList = new ArrayList<>();
    public interface FilterResultCallback {
        void onFilterSuccess(Bitmap bitmap, int requestCode);
        void onFilterFail(int requestCode);
    }

    private static FilterExecutor sInstance;

    public static void init() {
        sInstance = new FilterExecutor();
    }

    public static void destroy() {
        sInstance = null;
    }

    public static void execute(BitmapFilter filter, FilterResultCallback callback, int requestCode) {

    }

}
