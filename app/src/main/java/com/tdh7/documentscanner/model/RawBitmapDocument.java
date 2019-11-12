package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;

//import com.scanlibrary.ScanComponent;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Util;

public class RawBitmapDocument {
    private static final String TAG = "RawBitmapDocument";
    public RawBitmapDocument(Bitmap originalBitmap, int rotatedDegree, float[] viewPort, float[] points) {
        mOriginalBitmap = originalBitmap;
        mRotateDegree = rotatedDegree;
        mViewPort[0] = viewPort[0];
        mViewPort[1] = viewPort[1];
        System.arraycopy(points,0,mEdgePoints,0,8);
    }


    public final Bitmap mOriginalBitmap;
    public final float[] mEdgePoints = new float[8];
    public final int mRotateDegree;
    public final float[] mViewPort = new float[2];
}
