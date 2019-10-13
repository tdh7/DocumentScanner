package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class FilterImageView extends AppCompatImageView {
    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t, r, b);
    }
}
