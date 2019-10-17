package com.scanlibrary;

import android.graphics.Bitmap;

public final class ScanComponent {
    private ScanActivity mScanLibrary = new ScanActivity();

    public ScanComponent() {
    }

    public Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        return mScanLibrary.getScannedBitmap(bitmap,x1,y1,x2,y2,x3,y3,x4,y4);
    }

    public Bitmap getGrayBitmap(Bitmap bitmap) {
        return mScanLibrary.getGrayBitmap(bitmap);
    }

    public Bitmap getMagicColorBitmap(Bitmap bitmap) {
        return mScanLibrary.getMagicColorBitmap(bitmap);
    }

    public Bitmap getBWBitmap(Bitmap bitmap) {
        return mScanLibrary.getBWBitmap(bitmap);
    }

    public float[] getPoints(Bitmap bitmap) {
        return mScanLibrary.getPoints(bitmap);
    }
}
