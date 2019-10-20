package com.tdh7.documentscanner.controller.filter.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

public class QuickViewCanvasFilter extends CanvasFilter {
    private static final String TAG = "QuickViewCanvasFilter";

    public QuickViewCanvasFilter(float rotateValue) {
        setRotateValue(rotateValue);
    }

    public float getRotateValue() {
        return mRotateValue;
    }

    public QuickViewCanvasFilter setRotateValue(float rotateValue) {
        mRotateValue = rotateValue;
        mCos = (float) Math.cos(Math.toRadians(rotateValue));
        mSin = (float) Math.sin(Math.toRadians(rotateValue));
        mTan = (float) Math.tan(Math.toRadians(rotateValue));
        if(mFilterView !=null) mFilterView.invalidate();
        return this;
    }

    private float mRotateValue;
    private float mCos;
    private float mSin;
    private float mTan;
    private Matrix mMatrix = new Matrix();

    @Override
    public void filter(Bitmap original, Canvas canvas, RectF destRectF) {
        canvas.save();
        float srcW = original.getWidth();
        float srcH = original.getHeight();
        float destW = destRectF.width();
        float destH = destRectF.height();
        Log.d(TAG, "filter: src = ["+srcW+", "+srcH+"), dest = ["+destW+", "+srcH+"]");
        //canvas.translate(srcH*mSin*mCos, -srcH*mSin*mSin);
        //canvas.rotate(mRotateValue);
        // at 90 degree
      //  canvas.scale(destRectF.width()/original.getHeight(), destRectF.width()/original.getHeight(),destRectF.width()/2,destRectF.height()/2);

       // mMatrix.setRotate(mRotateValue);
       // mMatrix.preTranslate(srcH*mSin*mCos, - srcH*mSin*mSin);

        mMatrix.reset();
        mMatrix.postTranslate(-srcW/ 2f, -srcH/ 2);
        mMatrix.postRotate(mRotateValue);
        mMatrix.postTranslate(destW/2, destH/2);
        if(mRotateValue%90==0&&mRotateValue%180!=0) {
            if (destW > destH)
                mMatrix.postScale(destW / srcH, destW / srcH, destW / 2, destH / 2);
            else
                mMatrix.postScale(destH / srcW, destH / srcW, destW / 2, destH / 2);
        } else if(mRotateValue%180==0) {
            if(destW > destH)
                mMatrix.postScale(destW/srcW,destW/srcW,destW/2,destH/2);
            else mMatrix.postScale(destH/srcH,destH/srcH,destW/2,destH/2);
        }
        canvas.drawBitmap(original,mMatrix,null);

   //     canvas.drawBitmap();
        //canvas.drawBitmap(original,new Rect(0,0,original.getWidth(),original.getHeight()),destRectF,null);
        canvas.restore();
    }
}
