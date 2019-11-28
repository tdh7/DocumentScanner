package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Util;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class BitmapDocument {
    public static final int FILTER_NONE = 0;
    public static final int FILTER_MAGIC = 1;
    public static final int FILTER_GRAY_SCALE = 2;
    public static final int FILTER_BnW = 3;
    public static final int ACTION_ROTATE = 4;

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
    private float mRequestRotateAngle  = 0;
    public void postRotate() {
        mRotateAngle += 90;
    }

    public void setRotateAngle(float angle) {
        angle%=360;
        mRotateAngle = angle;
    }

    public Bitmap mOriginalBitmap;
    public Bitmap mDocumentBitmap;
    public Bitmap mTempBitmap = null;
    public final float[] mEdgePoints = new float[8];

    public void applyTempFilterOrRotate(int type) {
        switch (type) {
            case FILTER_NONE:
                mTempBitmap = null;
                mRotateAngle = 0;
                mRequestRotateAngle = 0;
                break;
            case FILTER_MAGIC:
                mTempBitmap = ScanUtils.getMagicColorBitmap(mDocumentBitmap);
                mRequestRotateAngle = mRotateAngle;
                break;
            case FILTER_GRAY_SCALE:
                mTempBitmap = ScanUtils.getGrayBitmap(mDocumentBitmap);
                mRequestRotateAngle = mRotateAngle;
                break;
            case FILTER_BnW:
                mTempBitmap = ScanUtils.getBWBitmap(mDocumentBitmap);
                mRequestRotateAngle = mRotateAngle;
                break;
            case ACTION_ROTATE:
                mRotateAngle+=90;
                mRotateAngle%=360;
                if(mTempBitmap==null) {
                    mTempBitmap = Bitmap.createBitmap(mDocumentBitmap);
                }
                mRequestRotateAngle = 90;
                break;
        }
        if(mTempBitmap!=null&&mRequestRotateAngle!=0) {
            mTempBitmap = Util.rotateBitmap(mTempBitmap, mRequestRotateAngle);
            mRequestRotateAngle = 0;
        }
    }

    public Bitmap buildResultBitmap() {
        // return temp bitmap if any
        if(mTempBitmap!=null) return mTempBitmap;
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
