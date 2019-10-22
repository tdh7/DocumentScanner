package com.tdh7.documentscanner.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;

import static android.content.Context.VIBRATOR_SERVICE;

public final class Util {
    public static Bitmap resizeBitmap(@NonNull Bitmap src, int maxForSmallerSize) {
        int width = src.getWidth();
        int height = src.getHeight();

        final int dstWidth;
        final int dstHeight;

        if (width < height) {
            if (maxForSmallerSize >= width) {
                return src;
            }
            float ratio = (float) height / width;
            dstWidth = maxForSmallerSize;
            dstHeight = Math.round(maxForSmallerSize * ratio);
        } else {
            if (maxForSmallerSize >= height) {
                return src;
            }
            float ratio = (float) width / height;
            dstWidth = Math.round(maxForSmallerSize * ratio);
            dstHeight = maxForSmallerSize;
        }
        return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    }

    /** getResizedBitmap method is used to Resized the Image according to custom width and height
     * @param image
     * @param newHeight (new desired height)
     * @param newWidth (new desired Width)
     * @return image (new resized image)
     * */
    public static Bitmap resizeBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create A matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap resizeThenRotate(Bitmap bitmap, int maxForSmallerSize, float angle) {
         Bitmap temp = Util.resizeBitmap(bitmap, maxForSmallerSize);
        if (angle != 0) temp = Util.rotateBitmap(temp,angle);
        return temp;
    }

    public static void vibrate(Context context) {
        if(context==null) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if(vibrator!=null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    public static Bitmap centerCropBitmap(Bitmap bitmap, float[] viewPort) {
        float wPerHDest = viewPort[0]/viewPort[1];
        float wOrg = bitmap.getWidth();
        float hOrg = bitmap.getHeight();
        float wPerHOrg = wOrg/hOrg;
        Bitmap result;
        if(wPerHOrg<wPerHDest) {
            float hDest = wOrg/wPerHDest;
            result = Bitmap.createBitmap(bitmap,0,(int)(hOrg/2 - hDest/2),(int)wOrg,(int)hDest);
        } else {
            // the result shorter than origin
            float wDest = hOrg*wPerHDest;
            result = Bitmap.createBitmap(bitmap,(int)(wOrg/2 - wDest/2),0,(int)wDest,(int)hOrg);
        }
        return result;
    }

    private final static float[] mInvalidEdges = new float[]{0,1,0,1,0,0,1,1};
    private final static float[] mTrueInvalidEdges = new float[]{0,0,1,1,1,0,0,1};
    /*
    0,1
    0,0
    1,0
    1,1
     */

    public static void getDefaultValue(float[] points) {
        if(points.length>7)
        System.arraycopy(points, 0, mInvalidEdges, 0, 8);
    }

    public static boolean isNotDefaultValue(float[] points) {
        for (int i = 0; i < 8; i++) {
            if(mInvalidEdges[i]!=points[i]) return true;
        }
        return false;
    }

    public static boolean isEdgeValid(float[] points) {
        return points!=null && points.length==8 && isNotDefaultValue(points);
    }

    public static void reverseToTrueDefaultValue(PointF[] pointFS) {
        pointFS[0].x = mTrueInvalidEdges[0];
        pointFS[1].x = mTrueInvalidEdges[1];
        pointFS[2].x = mTrueInvalidEdges[2];
        pointFS[3].x = mTrueInvalidEdges[3];

        pointFS[0].y = mTrueInvalidEdges[4];
        pointFS[1].y = mTrueInvalidEdges[5];
        pointFS[2].y = mTrueInvalidEdges[6];
        pointFS[3].y = mTrueInvalidEdges[7];
    }

    public static void reverseToDefaultValue(PointF[] pointFS) {
        pointFS[0].x = mInvalidEdges[0];
        pointFS[1].x = mInvalidEdges[1];
        pointFS[2].x = mInvalidEdges[2];
        pointFS[3].x = mInvalidEdges[3];

        pointFS[0].y = mInvalidEdges[4];
        pointFS[1].y = mInvalidEdges[5];
        pointFS[2].y = mInvalidEdges[6];
        pointFS[3].y = mInvalidEdges[7];
    }
}
