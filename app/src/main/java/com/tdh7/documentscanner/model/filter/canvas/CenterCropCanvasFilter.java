package com.tdh7.documentscanner.model.filter.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class CenterCropCanvasFilter implements CanvasFilter {
    @Override
    public void filter(Bitmap original, Canvas canvas, RectF destRectf) {
        canvas.save();
        canvas.drawBitmap(original,new Rect(0,0,original.getWidth(),original.getHeight()),destRectf,null);
        canvas.restore();
    }
}
