package com.tdh7.documentscanner.controller.filter.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.tdh7.documentscanner.controller.filter.FilterImageView;

public abstract class CanvasFilter {
    protected  FilterImageView mFilterView;

    public void attach(FilterImageView view) {
        mFilterView = view;
    }

    public void detach() {
        mFilterView = null;
    }
   public abstract void  filter(Bitmap original, Canvas canvas, RectF destRectF);
}
