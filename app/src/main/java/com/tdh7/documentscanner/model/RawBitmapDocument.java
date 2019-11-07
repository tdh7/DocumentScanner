package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.scanlibrary.ScanComponent;
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
    public BitmapDocument buildBitmapDocument(ScanComponent scanComponent) {
        Bitmap rotatedBitmap;
        if(mRotateDegree!=0) {
            rotatedBitmap = Util.rotateBitmap(mOriginalBitmap, mRotateDegree);
        } else rotatedBitmap = mOriginalBitmap;
        Bitmap croppedBitmap = Util.centerCropBitmap(rotatedBitmap,mViewPort);
        Bitmap result = Util.resizeBitmap(croppedBitmap,1080);

      float xRatio = (float) result.getWidth() / mViewPort[0];
      float yRatio = (float) result.getHeight() / mViewPort[1];

      float w = result.getWidth();
      float h = result.getHeight();
      /*
        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;*/

    //  Util.getDefaultValue(mEdgePoints);
    //    if(!Util.isEdgeValid(mEdgePoints))
        Util.reverseToTrueDefaultValue(mEdgePoints);
        float x1 = w*mEdgePoints[0];
        float x2 = w*mEdgePoints[1];
        float x3 = w*mEdgePoints[2];
        float x4 = w*mEdgePoints[3];

        float y1 = h*mEdgePoints[4];
        float y2 = h*mEdgePoints[5];
        float y3 = h*mEdgePoints[6];
        float y4 = h*mEdgePoints[7];

        Bitmap documentBitmap = scanComponent.getScannedBitmap(result, x1,y1,x2,y2,x3,y3,x4,y4);
        BitmapDocument document = new BitmapDocument();
        System.arraycopy(mEdgePoints,0,document.mEdgePoints,0,8);
        document.mOriginalBitmap = result;
        document.mDocumentBitmap = documentBitmap;
        return document;
    }
}
