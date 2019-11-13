package com.tdh7.documentscanner.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.tdh7.documentscanner.App;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

import static android.content.Context.VIBRATOR_SERVICE;

public final class Util {
    private static final String TAG = "Util";
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
        System.arraycopy(points, 0, mTrueInvalidEdges, 0, 8);
    }

    public static boolean isNotDefaultValue(float[] points) {
        int same = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(mInvalidEdges[i]==points[j]&&mInvalidEdges[i+4]==points[j+4]) {
                    same++;
                    break;
                }
            }
        }
        return same<4;
    }

    public static boolean isEdgeValid(float[] points) {
        return points!=null && points.length==8 && isNotDefaultValue(points);
    }

    public static void reverseToTrueDefaultValue(float[] pointFS) {
        System.arraycopy(mInvalidEdges,0,pointFS,0,8);
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

    public static Bitmap getBitmap(Context context, Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedimg, "r");

        Bitmap original = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor());
        return original;
    }

    public static void requestOtherAppToOpenThisFile(Context context, String directory, String file) {
        try {
            File filePath = new File(directory);
            File fileToOpen = new File(filePath, file);
            if(!fileToOpen.exists()) {
                Toasty.error(App.getInstance(),"Sorry, this document is no longer available").show();
            }

            final Uri data = FileProvider.getUriForFile(context, "com.tdh7.documentscanner.provider", fileToOpen);
            context.grantUriPermission(context.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String fileExtension = file.substring(file.lastIndexOf("."));
            Log.d(TAG, "onItemClick: extension " + fileExtension);
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            if (fileExtension.contains("apk")) {
                Log.d(TAG, "open as apk");
                intent.setDataAndType(data, "application/vnd.android.package-archive");
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            else
                intent.setData(data);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toasty.error(App.getInstance(),"Sorry, couldn't found any app that be able to open this file").show();
        } catch (Exception e) {
            Toasty.error(App.getInstance(),"Sorry, something went wrong :((").show();
            Log.d(TAG, "An exception when trying to open file: "+e.getMessage());
        }
    }
}
