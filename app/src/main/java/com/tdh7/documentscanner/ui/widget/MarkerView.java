package com.tdh7.documentscanner.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.tdh7.documentscanner.R;

import java.util.List;


public class MarkerView extends View {
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
    }

    private float w = 0;
    private float h = 0;

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

    public void setPoints(List<PointF> pointFMap) {
        if (pointFMap.size() == 4) {
            for (int i = 0; i < 4; i++) {
                mPointFs[i].set(pointFMap.get(i));
            }
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPointFs!=null) {
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
