package com.tdh7.documentscanner.model.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface BitmapFilter {
    Bitmap filter(Bitmap original);
}
