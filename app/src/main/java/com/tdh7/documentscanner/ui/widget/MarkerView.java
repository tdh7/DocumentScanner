package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.R;

import java.util.List;
public class MarkerView extends View {
    private static final String TAG = "MarkerView";
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
                new PointF(0,0),
                new PointF(1,0),
                new PointF(0,1),
                new PointF(1,1),
        };

        color = new int[] {
                0xffff6347,
                0xffffaa00,
                0xff3dcc3d,
                0xff1ABC9C};

        initPaint();
        setWillNotDraw(false);
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

    public void setPoints(SparseArray<PointF> pointFMap) {
        if (pointFMap.size() == 4) {
            for (int i = 0; i < 4; i++) {
                mPointFs[i].set(pointFMap.get(i));
            }
            invalidate();
        }
    }
    private boolean mShouldDrawMarker = false;

    public synchronized void setPoints(float[] points) {
        if(points==null || points.length!=8) {
            if(mShouldDrawMarker) {
                mShouldDrawMarker = false;
                invalidate();
            }
        } else {
            mShouldDrawMarker = true;
            mPointFs[0].x = points[0];
            mPointFs[1].x = points[1];
            mPointFs[2].x = points[2];
            mPointFs[3].x = points[3];

            mPointFs[0].y = points[4];
            mPointFs[1].y = points[5];
            mPointFs[2].y = points[6];
            mPointFs[3].y = points[7];
            Log.d(TAG, "setPoints: " +
                    "p[0] = ("+mPointFs[0].x+"; "+mPointFs[1].y+"), " +
                    "p[1] = ("+mPointFs[1].x+"; "+mPointFs[1].y+"), "+
                    "p[2] = ("+mPointFs[1].x+"; "+mPointFs[1].y+"), "+
                    "p[3] = ("+mPointFs[1].x+"; "+mPointFs[1].y+")");
            invalidate();

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPointFs!=null&&mShouldDrawMarker) {
            for (int i = 0, mPointFsLength = mPointFs.length; i < mPointFsLength; i++) {
                PointF p = mPointFs[i];
                paint.setColor(color[i]);
                paint.setStrokeWidth(oneDp * 2);
                canvas.drawCircle(p.x * w, p.y * h, oneDp * 4, paint);

                // paint.setColor(0xFFFFFFFF);
                //   paint.setStrokeWidth(oneDp);
                //      canvas.drawCircle(p.x * w, p.y * h, oneDp * 30, paint);
            }
        }
    }
}
