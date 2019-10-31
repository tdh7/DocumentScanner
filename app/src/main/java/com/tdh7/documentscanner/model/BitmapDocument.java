package com.tdh7.documentscanner.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;

public class BitmapDocument {
    public Bitmap mOriginalBitmap;
    public Bitmap mDocumentBitmap;
    public final PointF[] mPointFS = new PointF[4];

    public void recreateDocumentBitmap() {

    }
}
