package com.tdh7.documentscanner.ui.widget.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tdh7.documentscanner.R;

public class DmcConstraintLayout extends ConstraintLayout {
    public DmcConstraintLayout(Context context) {
        super(context);
        init(null);
    }

    public DmcConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DmcConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DmcConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private int mBackgroundId = View.NO_ID;

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            final int[] backgroundAttrs = new int[] {
                    android.R.attr.background};
             TypedArray t = getContext().obtainStyledAttributes(attrs, backgroundAttrs);
            // mPreviewColor = t.getColor(R.styleable.MarkerView_previewColor,0xFFFF9500);
            mBackgroundId = t.getResourceId(0, View.NO_ID);
            int intValue = t.getInteger(0,View.NO_ID);
            Drawable background = getResources().getDrawable(mBackgroundId);
            int color = getResources().getColor(mBackgroundId);
            int backColorAttr = R.attr.backColor;
            int backColorDark = 0x121212;
            int backColorLight = 0xffffff;
            t.recycle();
        }
    }

    public void updateTheme() {
        if(mBackgroundId!=View.NO_ID) {
            setBackgroundResource(mBackgroundId);
        }
    }
}
