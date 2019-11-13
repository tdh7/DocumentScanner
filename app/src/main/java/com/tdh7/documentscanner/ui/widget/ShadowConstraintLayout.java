package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tdh7.documentscanner.R;

public class ShadowConstraintLayout extends ConstraintLayout {
    public ShadowConstraintLayout(Context context) {
        super(context);
        init(null);
    }

    public ShadowConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ShadowConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ShadowConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setBackground(ShadowUtils.generateBackgroundWithShadow(this,android.R.color.white,
                R.dimen.dp_16,R.color.shadowColor,R.dimen.dp_16, Gravity.CENTER));
    }
}
