package com.tdh7.documentscanner.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.util.Util;

public class MarkerView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "MarkerView";
    public static final int STATE_JUST_CREATE = 0;
    public static final int STATE_MOTION_APPEAR = 1;
    public static final int STATE_PREVIEW = 2;
    public static final int STATE_MOTION_TO_CROPPER = 3;
    public static final int STATE_CROPPER = 4;
    public static final int STATE_MOTION_CROPPER_TO_PREVIEW = 5;

    private static final int DURATION_MOTION_TO_CROPPER = 450;
    private static final int DURATION_MOTION_TO_PREVIEW = 450;

    private int mState = STATE_JUST_CREATE;
    private int mNextState = STATE_MOTION_APPEAR;

    private boolean mIsBusy = false;
    private float mCurrentFractionValue = 1f;
    private float mCurrentAnimatedValue = 1f;

    // runMotion(STATE_JUST_CREATE, STATE_PREVIEW);
    public synchronized boolean runMotion(int from, int endState) {
        if(!mIsBusy&&!mValueAnimator.isRunning()) {
            mState = from;
            this.mNextState = endState;
            mIsBusy = true;
            update(0,0);
            return true;
        }
        return false;
    }

    private void update(float fraction, float animatedValue) {
        switch (mState) {
            case STATE_JUST_CREATE:
                endMotion();
                runMotion(STATE_MOTION_APPEAR, STATE_PREVIEW);
                break;
                case STATE_MOTION_APPEAR:
                    postInvalidate();
                    if(fraction==1) endMotion();
                    break;
            case STATE_PREVIEW:
                // do nothing
                break;
            case STATE_MOTION_TO_CROPPER:
                postInvalidate();
                break;
            case STATE_CROPPER:
                // do nothing
                break;
            case STATE_MOTION_CROPPER_TO_PREVIEW:
                postInvalidate();
                break;
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        update(animation.getAnimatedFraction(),(Float) animation.getAnimatedValue());
    }

    public boolean isPreviewMode() {
        return mState!=STATE_CROPPER&&mState!=STATE_MOTION_TO_CROPPER;
    }

    private synchronized void endMotion() {
        mState = mNextState;
        mIsBusy = false;
    }

    private Interpolator mInterpolator = new OvershootInterpolator();
    private ValueAnimator mValueAnimator;

    public MarkerView(Context context) {
        super(context);
        init(null);
    }

    public MarkerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MarkerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private Paint paint;
    private int[] color;

    private void init(AttributeSet attrs) {
        oneDp = getContext().getResources().getDimension(R.dimen.dp_unit);
        mPointFs = new PointF[] {
                new PointF(0,1),
                new PointF(0,0),
                new PointF(1,0),
                new PointF(1,1),
        };
        updateCoordPoints();

        color = new int[] {
                0xffff6347,
                0xffffaa00,
                0xff3dcc3d,
                0xff1ABC9C};

        initPaint();
        setWillNotDraw(false);

        if(attrs!=null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.MarkerView);
            mStillShowInvalid = t.getBoolean(R.styleable.MarkerView_showIfNoDetect,false);
            if(t.getInt(R.styleable.MarkerView_mode,0)==0)
                mState = STATE_PREVIEW;
            else mState = STATE_CROPPER;
            t.recycle();
        }

        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(DURATION_MOTION_TO_PREVIEW);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.start();
    }


    private float w = 0;
    private float h = 0;
    public float[] getViewPort() {
        return new float[] {w,h};
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        updateCoordPoints();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(com.scanlibrary.R.color.blue));
        paint.setStrokeWidth(2*oneDp);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    private float oneDp = 1;
    private PointF[] mPointFs;
    private PointF[] mCoordPointFs = new PointF[] {
            new PointF(0,1),
            new PointF(0,0),
            new PointF(1,0),
            new PointF(1,1),
    };

    private boolean mShouldDrawMarker = false;
    public synchronized void setPoints(float[] points) {
        if(Util.isEdgeValid(points)) {
            mShouldDrawMarker = true;
            mPointFs[0].x = points[0];
            mPointFs[1].x = points[1];
            mPointFs[2].x = points[2];
            mPointFs[3].x = points[3];

            mPointFs[0].y = points[4];
            mPointFs[1].y = points[5];
            mPointFs[2].y = points[6];
            mPointFs[3].y = points[7];
            updateCoordPoints();
            Log.d(TAG, "current marker: " +
                    "p[0] = ("+mPointFs[0].x+"; "+mPointFs[0].y+"), " +
                    "p[1] = ("+mPointFs[1].x+"; "+mPointFs[1].y+"), "+
                    "p[2] = ("+mPointFs[2].x+"; "+mPointFs[2].y+"), "+
                    "p[3] = ("+mPointFs[3].x+"; "+mPointFs[3].y+")");
            postInvalidate();

        } else if (mShouldDrawMarker) {
                Util.reverseToTrueDefaultValue(mPointFs);
                updateCoordPoints();
            mShouldDrawMarker = false;
            postInvalidate();
        }
    }

    public void updateCoordPoints() {
        mCoordPointFs[0].x = mPointFs[0].x*w;
        mCoordPointFs[1].x = mPointFs[1].x*w;
        mCoordPointFs[2].x = mPointFs[2].x*w;
        mCoordPointFs[3].x = mPointFs[3].x*w;

        mCoordPointFs[0].y = mPointFs[0].y*h;
        mCoordPointFs[1].y = mPointFs[1].y*h;
        mCoordPointFs[2].y = mPointFs[2].y*h;
        mCoordPointFs[3].y = mPointFs[3].y*h;
        getEdgesPath();
    }

    Path edgePath;
    private void drawEdges(Canvas canvas) {
        canvas.save();
        Path path = getEdgesPath();
        paint.setColor(0xFFFF9500);
        paint.setAlpha(80);
        paint.setStrokeWidth(oneDp*2);
        paint.setStyle(Paint.Style.FILL);
        edgePath.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path,paint);
        paint.setAlpha(225);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path,paint);
        canvas.restore();
    }
    
    private Path getEdgesPath() {
        if(edgePath ==null) edgePath = new Path();
        else edgePath.rewind();
        edgePath.moveTo(mCoordPointFs[0].x,mCoordPointFs[0].y);
        for(int i = 1;i<4;i++) {
            edgePath.lineTo(mCoordPointFs[i].x,mCoordPointFs[i].y);
        }
        //edgePath.moveTo();
        edgePath.close();
        return edgePath;
    }

    private void drawCropper(Canvas canvas) {
        edgePath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        paint.setColor(0x36000000);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(edgePath,paint);

/*        edgePath.setFillType(Path.FillType.EVEN_ODD);
        paint.setColor(0xFFFF9500);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp*2.5f);
        canvas.drawPath(edgePath,paint);*/

        paint.setColor(0xFFFF9500);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp*2.5f);
        paint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < 4; i++) {
            PointF p = mCoordPointFs[i];
            PointF pNext = (i==3) ? mCoordPointFs[0] : mCoordPointFs[i+1];


            float _8dp = 8*oneDp;
            float _12dp = 12*oneDp;
            canvas.drawCircle(p.x,p.y,_8dp,paint);
            float angle = (float) (Math.atan2(pNext.y-p.y, pNext.x - p.x) * 180 / Math.PI);
            float centerX = (pNext.x+p.x)/2f;
            float centerY = (pNext.y + p.y)/2f;

            float D = (float) (Math.sqrt((pNext.x - p.x)*(pNext.x - p.x) +(pNext.y-p.y)*(pNext.y - p.y))/2f);
            float startCenterX = centerX + (_12dp/D)*(p.x-centerX);
            float startCenterY = centerY + (_12dp/D)*(p.y - centerY);

            float endCenterX = centerX + (_12dp/D)*(pNext.x-centerX);
            float endCenterY = centerY + (_12dp/D)*(pNext.y - centerY);

            canvas.drawLine(p.x,p.y,startCenterX,startCenterY,paint);
            canvas.drawLine(pNext.x,pNext.y,endCenterX,endCenterY,paint);

            canvas.save();
            canvas.rotate(angle,centerX,centerY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(centerX-oneDp*12,centerY-5*oneDp,centerX+oneDp*12,centerY+5*oneDp,oneDp*2.5f,oneDp*2.5f,paint);
            } else canvas.drawRect(centerX-oneDp*12,centerY-5*oneDp,centerX+oneDp*12,centerY+5*oneDp,paint);
            canvas.restore();
        }
        canvas.getClipBounds(mClipRect);
        mClipRect.inset((int)(-12*oneDp),(int)(-12*oneDp));
        canvas.clipRect(mClipRect, Region.Op.INTERSECT);
    }

    private boolean mStillShowInvalid = false;

    @Override
    protected void onDraw(Canvas canvas) {


        if(mCoordPointFs!=null&&(mStillShowInvalid || mShouldDrawMarker)) {
            if(isPreviewMode())
            drawEdges(canvas);
            else drawCropper(canvas);
        }
    }

    private Rect mClipRect = new Rect();

    public void getPoints(float[] point) {
        if(point.length!=8) return;
        point[0] = mPointFs[0].x;
        point[1] = mPointFs[1].x;
        point[2] = mPointFs[2].x;
        point[3] = mPointFs[3].x;

        point[4] = mPointFs[0].y;
        point[5] = mPointFs[1].y;
        point[6] = mPointFs[2].y;
        point[7] = mPointFs[3].y;

    }
}