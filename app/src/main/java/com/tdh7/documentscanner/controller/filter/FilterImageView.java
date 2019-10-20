package com.tdh7.documentscanner.controller.filter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.filter.canvas.CanvasFilter;
import com.tdh7.documentscanner.controller.filter.canvas.QuickViewCanvasFilter;

import java.lang.reflect.Constructor;

public class FilterImageView extends View {
    private static final String TAG = "FilterImageView";
    public static final int FILTER_NONE = 0;
    public static final int FILTER_QUICK_VIEW = 1;
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

    boolean mDrawWithoutFilter = false;
    public void shouldDrawWithoutFilter(boolean draw) {
        if(mDrawWithoutFilter!=draw) {
            mDrawWithoutFilter = draw;
            invalidate();
        }
    }
    Bitmap mBitmap;
    Bitmap mSampleBitmap;

    Bitmap mFilteredBitmap;

    CanvasFilter mFilter;
    public void setFilter(CanvasFilter filter) {
        if(mFilter!=null) mFilter.detach();
        mFilter = filter;
        if(mFilter!=null) mFilter.attach(this);
        invalidate();
    }

    public CanvasFilter getFilter() {
        return mFilter;
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
        Context context = getContext();
        if(attrs!=null&&context!=null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.FilterImageView);
            int srcId = t.getResourceId(R.styleable.FilterImageView_bitmapSrc,0);
            if(srcId!=0) setBitmap(BitmapFactory.decodeResource(getResources(),srcId));
            shouldDrawWithoutFilter(t.getBoolean(R.styleable.FilterImageView_drawBitmapIfNoFilter,false));

            Object filterObject = null;

            String filterClass = t.getString(R.styleable.FilterImageView_filterClass);
            if(filterClass!=null) {
                ClassLoader loader = context.getClass().getClassLoader();
                if (loader != null) {
                    try {
                        boolean[] paraExists = new boolean[3];
                        int paraCount = 0;
                        paraExists[0] = t.hasValue(R.styleable.FilterImageView_filterParameter);
                        paraExists[1] = t.hasValue(R.styleable.FilterImageView_filterParameter1);
                        paraExists[2] = t.hasValue(R.styleable.FilterImageView_filterParameter2);

                        for (int i = 0; i < 3; i++) {
                            if (paraExists[i]) paraCount++;
                            else break;
                        }

                        float p0 = t.getFloat(R.styleable.FilterImageView_filterParameter, 0);
                        float p1 = t.getFloat(R.styleable.FilterImageView_filterParameter1, 0);
                        float p2 = t.getFloat(R.styleable.FilterImageView_filterParameter2, 0);

                        int filterType = t.getInt(R.styleable.FilterImageView_filter, 0);
                        if (filterType == FILTER_QUICK_VIEW && paraCount == 1)
                            filterObject = new QuickViewCanvasFilter(p0);
                        else {
                            Class<?> myClass = loader.loadClass(filterClass);
                            Constructor constructor;
                            switch (paraCount) {
                                case 0:
                                    try {
                                        filterObject = myClass.newInstance();
                                    } catch (Exception ignored) {
                                        Log.d(TAG, "no empty constructor found");
                                    }
                                    break;
                                case 1:
                                    try {
                                        constructor = myClass.getConstructor(float.class);
                                        filterObject = constructor.newInstance(p0);
                                    } catch (Exception ignored) {
                                        Log.d(TAG, "no constructor with 1 parameter found");
                                    }
                                    break;
                                case 2:
                                    try {
                                        constructor = myClass.getConstructor(float.class, float.class);
                                        filterObject = constructor.newInstance(p0, p1);
                                    } catch (Exception ignored) {
                                    }
                                    break;
                                case 3:
                                    try {
                                        constructor = myClass.getConstructor(float.class, float.class, float.class);
                                        filterObject = constructor.newInstance(p0, p1, p2);
                                    } catch (Exception ignored) {
                                    }
                                    break;
                            }
                        }
                    } catch(Exception e){
                        Log.d(TAG, "error on create filter instance");
                    }
                }
            }

            if(filterObject instanceof CanvasFilter) setFilter((CanvasFilter) filterObject);
            t.recycle();
        }
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawOriginal(canvas);
        if(mFilter!= null) {
            mFilter.filter(mBitmap,canvas,mDrawRectF);
        } else if(mDrawWithoutFilter) drawOriginal(canvas);
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
            canvas.drawBitmap(mBitmap,0,0,null);
        }
    }
}
