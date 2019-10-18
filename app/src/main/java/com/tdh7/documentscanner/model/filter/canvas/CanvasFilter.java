package com.tdh7.documentscanner.model.filter.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public interface CanvasFilter {
    void filter(Bitmap original, Canvas canvas, RectF destRectF);
}
