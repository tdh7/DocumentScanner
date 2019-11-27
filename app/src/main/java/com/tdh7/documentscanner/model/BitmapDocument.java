package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Util;

import java.util.ArrayList;

public class BitmapDocument {
    public static final int FILTER_NONE = 0;
    public static final int FILTER_MAGIC = 1;
    public static final int FILTER_GRAY_SCALE = 2;
    public static final int FILTER_BnW = 3;

    public int getFilter() {
        return mFilter;
    }

    public void setFilter(int filter) {
        mFilter = filter;
    }

    private int mFilter = FILTER_NONE;

    public float getRotateAngle() {
        return mRotateAngle;
    }

    private float mRotateAngle = 0;
    public void postRotate() {
        mRotateAngle += 90;
    }

    public void setRotateAngle(float angle) {
        angle%=360;
        mRotateAngle = angle;
    }

    public Bitmap mOriginalBitmap;
    public Bitmap mDocumentBitmap;
    public Bitmap mTempBitmap;
    public final float[] mEdgePoints = new float[8];

    public Bitmap buildResultBitmap() {
        Bitmap bitmap ;
        if(mRotateAngle!=0) bitmap = Util.rotateBitmap(mDocumentBitmap,mRotateAngle);
        else bitmap = Bitmap.createBitmap(mDocumentBitmap);

        switch (mFilter) {
            case FILTER_MAGIC:
                bitmap = ScanUtils.getMagicColorBitmap(bitmap);
                break;
            case FILTER_GRAY_SCALE:
                bitmap = ScanUtils.getGrayBitmap(bitmap);
                break;
            case FILTER_BnW:
                bitmap = ScanUtils.getBWBitmap(bitmap);
                break;
        }

        return bitmap;
    }
}
