package com.tdh7.documentscanner.model.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface Filter {
    Bitmap filter(Bitmap original);
    //void filter(Canvas canvas);
}
