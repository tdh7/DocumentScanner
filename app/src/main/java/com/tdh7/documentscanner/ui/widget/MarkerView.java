package com.tdh7.documentscanner.ui.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.util.Util;

public class MarkerView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "MarkerView";
    public static final int STATE_HIDDEN = 0;
    public static final int STATE_PREVIEW = 1;
    public static final int STATE_CROPPER = 2;

    private static final int STATE_MOTION_HIDDEN_TO_PREVIEW = 3;
    private static final int STATE_MOTION_HIDDEN_TO_CROPPER = 4;
    
    private static final int STATE_MOTION_PREVIEW_TO_CROPPER = 5;
    private static final int STATE_MOTION_CROPPER_TO_PREVIEW = 6;
    private static final int STATE_MOTION_CROPPER_TO_HIDDEN = 7;
    private static final int STATE_MOTION_PREVIEW_TO_HIDDEN = 8;

    private static final int DURATION_MOTION_TO_CROPPER = 650;
    private static final int DURATION_MOTION_TO_PREVIEW = 650;

    private int mState = STATE_HIDDEN;
    private int mNextState = STATE_PREVIEW;

    private float mCurrentFractionValue = 1f;
    private float mCurrentAnimatedValue = 1f;
    
    public boolean setState(int nextState) {
        // state is STATE_PREVIEW, STATE_CROPPER, or STATE_HIDDEN
        if (isBusy()) return false;
        mNextState = nextState;
        switch (mState) {
            case STATE_HIDDEN:
                switch (nextState) {
                    case STATE_PREVIEW:
                        runMotionThenSwitchState(STATE_MOTION_HIDDEN_TO_PREVIEW);
                        break;
                    case STATE_CROPPER:
                        runMotionThenSwitchState(STATE_MOTION_HIDDEN_TO_CROPPER,650,650,mInterpolator);
                        break;
                }
                break;
            case STATE_PREVIEW:
                switch (nextState) {
                    case STATE_HIDDEN:
                        runMotionThenSwitchState(STATE_MOTION_PREVIEW_TO_HIDDEN);
                        break;
                    case STATE_CROPPER:
                        runMotionThenSwitchState(STATE_MOTION_PREVIEW_TO_CROPPER,650,650,mInterpolator);
                        break;
                }
            case STATE_CROPPER:
                switch (nextState) {
                    case STATE_HIDDEN:
                        runMotionThenSwitchState(STATE_MOTION_CROPPER_TO_HIDDEN);
                        break;
                    case STATE_PREVIEW:
                        runMotionThenSwitchState(STATE_MOTION_CROPPER_TO_PREVIEW);
                        break;
                }

        }
        return true;
    }

    private void runMotionThenSwitchState(int motionState) {
        runMotionThenSwitchState(motionState,DURATION_MOTION_TO_PREVIEW,0,mInterpolator);
    }
    private void runMotionThenSwitchState(int motionState, int duration, int delay, Interpolator interpolator) {
        if(isBusy()) mValueAnimator.end();
        mState = motionState;
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(interpolator);
        mValueAnimator.setStartDelay(delay);
        mCurrentFractionValue = 0;
        mCurrentAnimatedValue = 0;
        mValueAnimator.start();
    }

    private boolean update() {
        switch (mState) {
            case STATE_HIDDEN:
                // Do nothing
                return false;
            case STATE_PREVIEW:
                return true;
            case STATE_CROPPER:
                return true;
            case STATE_MOTION_HIDDEN_TO_PREVIEW:
                return true;
            case STATE_MOTION_HIDDEN_TO_CROPPER:
                return true;
            case STATE_MOTION_PREVIEW_TO_CROPPER:
                return true;
            case STATE_MOTION_CROPPER_TO_PREVIEW:
                return true;
            case STATE_MOTION_CROPPER_TO_HIDDEN:
                return true;
            case STATE_MOTION_PREVIEW_TO_HIDDEN:
                return true;

        }
        return false;
    }

    public void doDraw(Canvas canvas) {
        switch (mState) {
            case STATE_HIDDEN:
                // Do nothing
                return;
            case STATE_PREVIEW:
                drawPreviewPath(canvas,1);
                return;
            case STATE_CROPPER:
                drawCropper(canvas,1);
                return;
            case STATE_MOTION_HIDDEN_TO_PREVIEW:
                drawPreviewPath(canvas,mCurrentAnimatedValue);
                return;
            case STATE_MOTION_HIDDEN_TO_CROPPER:
                drawCropper(canvas,mCurrentAnimatedValue);
                return;
            case STATE_MOTION_PREVIEW_TO_CROPPER:
                if(mShouldDrawMarker)
                drawPreviewPath(canvas,1-mCurrentFractionValue);
                drawCropper(canvas,mCurrentAnimatedValue);
                return;
            case STATE_MOTION_CROPPER_TO_PREVIEW:
                if(mShouldDrawMarker)
                    drawPreviewPath(canvas,mCurrentAnimatedValue);
                drawCropper(canvas,1-mCurrentFractionValue);
                return;
            case STATE_MOTION_CROPPER_TO_HIDDEN:
                drawCropper(canvas,1 - mCurrentFractionValue);
                return;
            case STATE_MOTION_PREVIEW_TO_HIDDEN:
                drawPreviewPath(canvas,1-mCurrentFractionValue);

        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentFractionValue = animation.getAnimatedFraction();
        mCurrentAnimatedValue = (float) animation.getAnimatedValue();
        if(update())
            invalidate();
        if(mCurrentFractionValue==1) resetValue();
    }

    public void setPaddingAndInvalidate(int start, int top, int right, int bottom) {
        setPadding(start,top,right,bottom);
        updateValues();
    }

    public boolean isPreviewMode() {
        return mState==STATE_PREVIEW||mNextState==STATE_PREVIEW;
    }

    private synchronized void resetValue() {
        mCurrentAnimatedValue = 1;
        mCurrentFractionValue = 1;
        mState = mNextState;
    }

    private int mPreviewColor = 0xffff9500;
    private int mCropperColor = 0xffFF9500;

    private Interpolator mInterpolator = new OvershootInterpolator();
    private ValueAnimator mValueAnimator;
    
    public boolean isBusy() {
    	return mValueAnimator.isRunning();
    }

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
        _8dp = oneDp*8;
        _12dp = oneDp*12;
        _25dp = oneDp*25;
        _35dp = oneDp*35;
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
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(DURATION_MOTION_TO_PREVIEW);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.addUpdateListener(this);

        if(attrs!=null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.MarkerView);
            mPreviewColor = t.getColor(R.styleable.MarkerView_previewColor,0xFFFF9500);
            mCropperColor = t.getColor(R.styleable.MarkerView_cropperColor,0xFFFF9500);
            mStillShowInvalid = t.getBoolean(R.styleable.MarkerView_showIfNoDetect,false);
            int state = t.getInt(R.styleable.MarkerView_mode,STATE_HIDDEN);
            mState= t.getInt(R.styleable.MarkerView_startMode,STATE_HIDDEN);
            if(state!=STATE_HIDDEN)
            setState(state);
            t.recycle();
        }
    }

    private int mDrawTop;
    private int mDrawBottom;
    private int mDrawLeft;
    private int mDrawRight;
    private int mDrawWidth;
    private int mDrawHeight;

    public float[] getViewPort() {
        return new float[] {mDrawWidth, mDrawHeight};
    }

    private int mViewWidth = 0, mViewHeight = 0;
    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
      mViewWidth = w;
      mViewHeight = h;
      updateValues();
    }

    public void updateValues() {
        mDrawTop = getPaddingTop();
        mDrawBottom = mViewHeight - getPaddingBottom();
        mDrawLeft = getPaddingStart();
        mDrawRight = mViewWidth - getPaddingEnd();

        mDrawWidth = mDrawRight - mDrawLeft;
        mDrawHeight = mDrawBottom - mDrawTop;
        updateCoordPoints();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.scanner_text_color));
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
        mCoordPointFs[0].x = mPointFs[0].x* mDrawWidth;
        mCoordPointFs[1].x = mPointFs[1].x* mDrawWidth;
        mCoordPointFs[2].x = mPointFs[2].x* mDrawWidth;
        mCoordPointFs[3].x = mPointFs[3].x* mDrawWidth;

        mCoordPointFs[0].y = mPointFs[0].y* mDrawHeight;
        mCoordPointFs[1].y = mPointFs[1].y* mDrawHeight;
        mCoordPointFs[2].y = mPointFs[2].y* mDrawHeight;
        mCoordPointFs[3].y = mPointFs[3].y* mDrawHeight;
        recalculateValue();
    }

    public void updatePointsByCoordPoints() {
        for (int i = 0; i <4; i++) {
            if(mCoordPointFs[i].x<0) mCoordPointFs[i].x = 0;
            else if(mCoordPointFs[i].x> mDrawWidth) mCoordPointFs[i].x = mDrawWidth;

            if(mCoordPointFs[i].y<0) mCoordPointFs[i].y = 0;
            else if(mCoordPointFs[i].y > mDrawHeight) mCoordPointFs[i].y = mDrawHeight;
        }
        mPointFs[0].x = mCoordPointFs[0].x/ mDrawWidth;
        mPointFs[1].x = mCoordPointFs[1].x/ mDrawWidth;
        mPointFs[2].x = mCoordPointFs[2].x/ mDrawWidth;
        mPointFs[3].x = mCoordPointFs[3].x/ mDrawWidth;

        mPointFs[0].y = mCoordPointFs[0].y/ mDrawHeight;
        mPointFs[1].y = mCoordPointFs[1].y/ mDrawHeight;
        mPointFs[2].y = mCoordPointFs[2].y/ mDrawHeight;
        mPointFs[3].y = mCoordPointFs[3].y/ mDrawHeight;
        recalculateValue();
    }
    public void recalculateValue() {
        updateEdgesPath();
        updateOpEdgesPath();
        for (int i = 0; i < 4; i++) {
            PointF p = mCoordPointFs[i];
            PointF pNext = (i==3) ? mCoordPointFs[0] : mCoordPointFs[i+1];
           mCenterAngles[i] = (float) (Math.atan2(pNext.y-p.y, pNext.x - p.x) * 180 / Math.PI);
            mEdgeCenters[i].x = (pNext.x+p.x)/2f;
            mEdgeCenters[i].y = (pNext.y + p.y)/2f;

            mDs[i] = (float) (Math.sqrt((pNext.x - p.x)*(pNext.x - p.x) +(pNext.y-p.y)*(pNext.y - p.y))/2f);
        }
    }

    private PointF[] mEdgeCenters = new PointF[] {
            new PointF(0,0),
            new PointF(0,0),
            new PointF(0,0),
            new PointF(0,0)
    };

    private float[] mCenterAngles = new float[4];
    private float[] mDs = new float[4];

    float _35dp;
    float _25dp;
    float _8dp;
    float _12dp;
    Path edgePath;
    private void drawPreviewPath(Canvas canvas, float animatedValue) {
        canvas.save();
        Path path = getEdgePath();
        paint.setColor(mPreviewColor);
        float animatedAlpha = (animatedValue>1) ? 1 : ((animatedValue>0) ? animatedValue : 0);
        paint.setAlpha((int)(80*animatedAlpha));
        paint.setStyle(Paint.Style.FILL);
        edgePath.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path,paint);
        paint.setAlpha((int)(225*animatedAlpha));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp*2f*animatedAlpha);
        canvas.drawPath(path,paint);
        canvas.restore();
    }

    private Path mOpEdgePath;
    private Path getOpEdgePath() {
        if(mOpEdgePath==null) updateOpEdgesPath();
        return mOpEdgePath;
    }

    private Path getEdgePath() {
        if(edgePath==null) updateEdgesPath();
        return edgePath;
    }

    private void updateOpEdgesPath() {
        if(mOpEdgePath ==null) mOpEdgePath = new Path();
        else mOpEdgePath.rewind();
        // outside rect
        mOpEdgePath.moveTo(mDrawLeft,mDrawTop);
        mOpEdgePath.lineTo(mDrawRight,mDrawTop);
        mOpEdgePath.lineTo(mDrawRight,mDrawBottom);
        mOpEdgePath.lineTo(mDrawLeft,mDrawBottom);
        mOpEdgePath.close();
        mOpEdgePath.moveTo(mDrawLeft + mCoordPointFs[0].x ,mDrawTop +mCoordPointFs[0].y);
        for(int i = 1;i<4;i++) {
            mOpEdgePath.lineTo(mDrawLeft + mCoordPointFs[i].x, mDrawTop + mCoordPointFs[i].y);
        }
        //mOpEdgePath.moveTo();
        mOpEdgePath.close();
    }
    
    private void updateEdgesPath() {
        if(edgePath ==null) edgePath = new Path();
        else edgePath.rewind();
        edgePath.moveTo(mDrawLeft + mCoordPointFs[0].x ,mDrawTop +mCoordPointFs[0].y);
        for(int i = 1;i<4;i++) {
            edgePath.lineTo(mDrawLeft + mCoordPointFs[i].x, mDrawTop + mCoordPointFs[i].y);
        }
        //edgePath.moveTo();
        edgePath.close();
    }

    float startCenterX, startCenterY,endCenterX, endCenterY;
    private void drawCropper(Canvas canvas, float animatedValue) {
        Path opPath = getOpEdgePath();
        opPath.setFillType(Path.FillType.EVEN_ODD);
        paint.setColor(Color.BLACK);
        float animatedAlpha = (animatedValue>1) ? 1 : ((animatedValue>0) ? animatedValue : 0);
        paint.setAlpha((int)(0x66*animatedAlpha));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(opPath,paint);

/*        edgePath.setFillType(Path.FillType.EVEN_ODD);
        paint.setColor(0xFFFF9500);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp*2.5f);
        canvas.drawPath(edgePath,paint);*/

        paint.setColor(mCropperColor);
        paint.setAlpha((int)(255*animatedAlpha));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp*2.5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < 4; i++) {
            PointF p = mCoordPointFs[i];
            PointF pNext = (i==3) ? mCoordPointFs[0] : mCoordPointFs[i+1];

            canvas.drawCircle(p.x + mDrawLeft,p.y + mDrawTop,_12dp*animatedValue,paint);

            startCenterX =  mEdgeCenters[i].x + (_12dp/mDs[i])*(p.x-mEdgeCenters[i].x);
            startCenterY = mEdgeCenters[i].y + (_12dp/mDs[i])*(p.y - mEdgeCenters[i].y);

            endCenterX = mEdgeCenters[i].x + (_12dp/mDs[i])*(pNext.x-mEdgeCenters[i].x);
            endCenterY = mEdgeCenters[i].y + (_12dp/mDs[i])*(pNext.y - mEdgeCenters[i].y);

            canvas.drawLine(mDrawLeft +p.x ,mDrawTop +p.y, mDrawLeft +startCenterX,mDrawTop +startCenterY,paint);
            canvas.drawLine(mDrawLeft +pNext.x,mDrawTop +pNext.y,mDrawLeft +endCenterX,mDrawTop +endCenterY,paint);

            canvas.save();
            canvas.rotate(mCenterAngles[i],mDrawLeft + mEdgeCenters[i].x,mDrawTop +mEdgeCenters[i].y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(
                        mDrawLeft +mEdgeCenters[i].x-oneDp*12,
                        mDrawTop +mEdgeCenters[i].y-5*oneDp*animatedValue,
                        mDrawLeft + mEdgeCenters[i].x+oneDp*12,
                        mDrawTop + mEdgeCenters[i].y+5*oneDp*animatedValue,oneDp*2.5f,oneDp*2.5f,paint);
            } else canvas.drawRect(
                    mDrawLeft + mEdgeCenters[i].x-oneDp*12,
                    mDrawTop + mEdgeCenters[i].y-5*oneDp,
                    mDrawLeft + mEdgeCenters[i].x+oneDp*12,
                    mDrawTop + mEdgeCenters[i].y+5*oneDp,paint);
            canvas.restore();
        }

        // Draw touch zone
    /*    paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(oneDp);
        for (int i = 0; i < 4; i++) {
            canvas.drawCircle(mCoordPointFs[i].x,mCoordPointFs[i].y,_25dp,paint);
            canvas.drawCircle(mEdgeCenters[i].x,mEdgeCenters[i].y,_25dp,paint);
        }*/
    }

    private boolean mStillShowInvalid = false;

    @Override
    protected void onDraw(Canvas canvas) {

        if(mCoordPointFs!=null&&(mStillShowInvalid || mShouldDrawMarker)) {
            doDraw(canvas);
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

    private int whichPoint(float x, float y) {
        float d2;
        final float radius2 = _35dp*_35dp;
        float minD2 = radius2;
        int minPos = -1;
        for (int i = 0; i < 4; i++) {
            d2 = (mCoordPointFs[i].x - x)*(mCoordPointFs[i].x - x) +(mCoordPointFs[i].y-y)*(mCoordPointFs[i].y - y);
                if(d2<minD2) {
                    minPos = i;
                    minD2 = d2;
            }
        }

        for (int i = 0; i < 4; i++) {
            d2 = (mEdgeCenters[i].x - x)*(mEdgeCenters[i].x - x) + (mEdgeCenters[i].y - y)*(mEdgeCenters[i].y - y);
            if(d2<minD2) {
                minPos = i+4;
                minD2 = d2;
            }
        }
        return minPos;
    }

    private boolean mIsTouchDown = false;
    private float mTouchDownX, mTouchDownY;
    private int mWhichEditingPoint = -1;

    private void touchDown(float x, float y) {
        mIsTouchDown = true;
        mTouchDownX = x;
        mTouchDownY = y;
        mWhichEditingPoint = whichPoint(x - mDrawLeft, y - mDrawTop);

        int which = mWhichEditingPoint;
        if(which>3) {
            which -=4;
            oldWhichX = mCoordPointFs[which].x;
            oldWhichY = mCoordPointFs[which].y;
            int whichPlus = (which==3) ? 0 : which+1;
            oldWhichX2 = mCoordPointFs[whichPlus].x;
            oldWhichY2 = mCoordPointFs[whichPlus].y;
        } else if (which>-1) {
            oldWhichX = mCoordPointFs[which].x;
            oldWhichY = mCoordPointFs[which].y;
        }
        invalidate();
    }

    float oldWhichX, oldWhichY;
    float oldWhichX2, oldWhichY2;

    private void touchAssign(float x, float y) {
        int which= mWhichEditingPoint;
        float deltaX =  x - mTouchDownX;
        float deltaY = y - mTouchDownY;
        if(which>3) {
            which-=4;
            mCoordPointFs[which].x = oldWhichX + deltaX;
            mCoordPointFs[which].y = oldWhichY + deltaY;

            int whichPlus = (which==3) ? 0 : which + 1;
            mCoordPointFs[whichPlus].x = oldWhichX2 + deltaX;
            mCoordPointFs[whichPlus].y = oldWhichY2 + deltaY;
            // center edge
        } else if(which>-1) {
            mCoordPointFs[which].x = oldWhichX +deltaX;
            mCoordPointFs[which].y = oldWhichY + deltaY;
            // vertice
        }
        updatePointsByCoordPoints();
        invalidate();
    }

    private void touchMove(float x, float y) {
        if(!mIsTouchDown) {
            touchDown(x,y);
            return;
        }

        touchAssign(x,y);
    }

    private void touchUp(float x, float y) {
        mIsTouchDown = false;
        touchAssign(x,y);
        mWhichEditingPoint = -1;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mState!=STATE_CROPPER) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event.getX(),event.getY());
                break;
        }
        return true;
    }
}