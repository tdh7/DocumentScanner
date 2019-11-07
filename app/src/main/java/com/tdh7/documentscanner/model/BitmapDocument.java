package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;

public class BitmapDocument {
    public Bitmap mOriginalBitmap;
    public Bitmap mDocumentBitmap;
    public final float[] mEdgePoints = new float[8];

    public void recreateDocumentBitmap() {

    }
}
