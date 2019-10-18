package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RotateAbleImageView extends AppCompatImageView {
    public RotateAbleImageView(Context context) {
        super(context);
    }

    public RotateAbleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateAbleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mDrawTop;
    private int mDrawBottom;
    private int mDrawLeft;
    private int mDrawRight;
    private int mDrawWidth;
    private int mDrawHeight;
    private int mCenterX;
    private int mCenterY;

    private RectF mDrawRectF = new RectF();
    private RectF mOriginRectF = new RectF();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawTop = getPaddingTop();
        mDrawBottom = h - getPaddingBottom();
        mDrawLeft = getPaddingStart();
        mDrawRight = w - getPaddingEnd();

        mDrawWidth = mDrawRight - mDrawLeft;
        mDrawHeight = mDrawBottom - mDrawTop;

        mCenterX = mDrawLeft + mDrawWidth/2;
        mCenterY = mDrawTop + mDrawHeight/2;

        mDrawRectF.left = mDrawLeft;
        mDrawRectF.top = mDrawTop;
        mDrawRectF.right = mDrawRight;
        mDrawRectF.bottom = mDrawBottom;
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = r - l;
        final int viewHeight = b -t;
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();

        mOriginRectF.top = mOriginRectF.left = 0;
        mOriginRectF.right = drawableWidth;
        mOriginRectF.bottom = drawableHeight;

       // matrix.setRectToRect(mOriginRectF,mDrawRectF, Matrix.ScaleToFit.START);
        matrix.postRotate(-270f,drawableWidth/2,drawableHeight/2);
        matrix.postScale((float) viewWidth/drawableWidth, (float)viewHeight/drawableHeight);
        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }
}
