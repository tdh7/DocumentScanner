package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tdh7.documentscanner.R;

public class ScaleSwipeRefreshLayout extends SwipeRefreshLayout {
    public ScaleSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ScaleSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.flatOrange);
        setProgressViewOffset(true,getProgressViewStartOffset(),getProgressViewEndOffset());
    }

}
