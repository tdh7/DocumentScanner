package com.tdh7.documentscanner.model.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.model.filter.canvas.CanvasFilter;

public class FilterImageView extends View {
    public FilterImageView(Context context) {
        super(context);
        init(null);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    Bitmap mBitmap;
    Bitmap mSampleBitmap;

    Bitmap mFilteredBitmap;

    CanvasFilter mFilter;
    public void setFilter(CanvasFilter filter) {
        mFilter = filter;

    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mOriginRectF.left = 0;
        mOriginRectF.top = 0;
        mOriginRectF.right = bitmap.getWidth();
        mOriginRectF.bottom = bitmap.getWidth();
        requestUpdate();
    }

    protected void requestUpdate(){
        invalidate();
    }


    private void init(AttributeSet attrs) {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawOriginal(canvas);
        if(mFilter!= null) {
            mFilter.filter(mBitmap,canvas,mDrawRectF);
        }
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
    private Rect mOriginRectF = new Rect();

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

    private boolean isOriginAvailble() {
        return mBitmap != null && !mBitmap.isRecycled();
    }


    private void drawOriginal(Canvas canvas) {
        if(isOriginAvailble()) {
            canvas.drawBitmap(mBitmap,mOriginRectF,mDrawRectF,null);
        }
    }
}
