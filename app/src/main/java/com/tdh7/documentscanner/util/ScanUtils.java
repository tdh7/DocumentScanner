package com.tdh7.documentscanner.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;

import com.tdh7.documentscanner.model.BitmapDocument;
import com.tdh7.documentscanner.model.RawBitmapDocument;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 * This class provides utilities for camera.
 */

public class ScanUtils {
    private static final String TAG = ScanUtils.class.getSimpleName();

    public static boolean compareFloats(double left, double right) {
        double epsilon = 0.00000001;
        return Math.abs(left - right) < epsilon;
    }

    public static Camera.Size determinePictureSize(Camera camera, Camera.Size previewSize) {
        if (camera == null) return null;
        Camera.Parameters cameraParams = camera.getParameters();
        List<Camera.Size> pictureSizeList = cameraParams.getSupportedPictureSizes();
        Collections.sort(pictureSizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size size1, Camera.Size size2) {
                Double h1 = Math.sqrt(size1.width * size1.width + size1.height * size1.height);
                Double h2 = Math.sqrt(size2.width * size2.width + size2.height * size2.height);
                return h2.compareTo(h1);
            }
        });
        Camera.Size retSize = null;

        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : pictureSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
            if (ScanUtils.compareFloats(deltaRatio, 0)) {
                break;
            }
        }

        return retSize;
    }

    public static Camera.Size getOptimalPreviewSize(Camera camera, int w, int h) {
        if (camera == null) return null;
        final double targetRatio = (double) h / w;
        Camera.Parameters cameraParams = camera.getParameters();
        List<Camera.Size> previewSizeList = cameraParams.getSupportedPreviewSizes();
        Collections.sort(previewSizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size size1, Camera.Size size2) {
                double ratio1 = (double) size1.width / size1.height;
                double ratio2 = (double) size2.width / size2.height;
                Double ratioDiff1 = Math.abs(ratio1 - targetRatio);
                Double ratioDiff2 = Math.abs(ratio2 - targetRatio);
                if (ScanUtils.compareFloats(ratioDiff1, ratioDiff2)) {
                    Double h1 = Math.sqrt(size1.width * size1.width + size1.height * size1.height);
                    Double h2 = Math.sqrt(size2.width * size2.width + size2.height * size2.height);
                    return h2.compareTo(h1);
                }
                return ratioDiff1.compareTo(ratioDiff2);
            }
        });

        return previewSizeList.get(0);
    }

    public static int getDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        DisplayMetrics dm = new DisplayMetrics();

        Camera.getCameraInfo(cameraId, info);
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int displayOrientation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (info.orientation - degrees + 360) % 360;
        }
        return displayOrientation;
    }

    public static Camera.Size getOptimalPictureSize(Camera camera, final int width, final int height, final Camera.Size previewSize) {
        if (camera == null) return null;
        Camera.Parameters cameraParams = camera.getParameters();
        List<Camera.Size> supportedSizes = cameraParams.getSupportedPictureSizes();

        Camera.Size size = camera.new Size(width, height);

        // convert to landscape if necessary
        if (size.width < size.height) {
            int temp = size.width;
            size.width = size.height;
            size.height = temp;
        }

        Camera.Size requestedSize = camera.new Size(size.width, size.height);

        double previewAspectRatio = (double) previewSize.width / (double) previewSize.height;

        if (previewAspectRatio < 1.0) {
            // reset ratio to landscape
            previewAspectRatio = 1.0 / previewAspectRatio;
        }

        Log.d(TAG, "CameraPreview previewAspectRatio " + previewAspectRatio);

        double aspectTolerance = 0.1;
        double bestDifference = Double.MAX_VALUE;

        for (int i = 0; i < supportedSizes.size(); i++) {
            Camera.Size supportedSize = supportedSizes.get(i);

            // Perfect match
            if (supportedSize.equals(requestedSize)) {
                Log.d(TAG, "CameraPreview optimalPictureSize " + supportedSize.width + 'x' + supportedSize.height);
                return supportedSize;
            }

            double difference = Math.abs(previewAspectRatio - ((double) supportedSize.width / (double) supportedSize.height));

            if (difference < bestDifference - aspectTolerance) {
                // better aspectRatio found
                if ((width != 0 && height != 0) || (supportedSize.width * supportedSize.height < 2048 * 1024)) {
                    size.width = supportedSize.width;
                    size.height = supportedSize.height;
                    bestDifference = difference;
                }
            } else if (difference < bestDifference + aspectTolerance) {
                // same aspectRatio found (within tolerance)
                if (width == 0 || height == 0) {
                    // set highest supported resolution below 2 Megapixel
                    if ((size.width < supportedSize.width) && (supportedSize.width * supportedSize.height < 2048 * 1024)) {
                        size.width = supportedSize.width;
                        size.height = supportedSize.height;
                    }
                } else {
                    // check if this pictureSize closer to requested width and height
                    if (Math.abs(width * height - supportedSize.width * supportedSize.height) < Math.abs(width * height - size.width * size.height)) {
                        size.width = supportedSize.width;
                        size.height = supportedSize.height;
                    }
                }
            }
        }
        Log.d(TAG, "CameraPreview optimalPictureSize " + size.width + 'x' + size.height);
        return size;
    }

    public static Camera.Size getOptimalPreviewSize(int displayOrientation, List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (displayOrientation == 90 || displayOrientation == 270) {
            targetRatio = (double) h / w;
        }

        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Log.d("optimal preview size", "w: " + optimalSize.width + " h: " + optimalSize.height);
        return optimalSize;
    }


    public static int configureCameraAngle(Activity activity) {
        int angle;

        Display display = activity.getWindowManager().getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                angle = 90; // This is camera orientation
                break;
            case Surface.ROTATION_90:
                angle = 0;
                break;
            case Surface.ROTATION_180:
                angle = 270;
                break;
            case Surface.ROTATION_270:
                angle = 180;
                break;
            default:
                angle = 90;
                break;
        }

        return angle;
    }

    public static Quadrilateral detectLargestQuadrilateral(Mat mat) {
        Mat mGrayMat = new Mat(mat.rows(), mat.cols(), CV_8UC1);
        Imgproc.cvtColor(mat, mGrayMat, Imgproc.COLOR_BGR2GRAY, 4);
        Imgproc.threshold(mGrayMat, mGrayMat, 150, 255, THRESH_BINARY + THRESH_OTSU);

        List<MatOfPoint> largestContour = findLargestContour(mGrayMat);
        if (null != largestContour) {
            Quadrilateral mLargestRect = findQuadrilateral(largestContour);
            if (mLargestRect != null)
                return mLargestRect;
        }
        return null;
    }

    public static double getMaxCosine(double maxCosine, Point[] approxPoints) {
        Log.i(TAG, "ANGLES ARE:");
        for (int i = 2; i < 5; i++) {
            double cosine = Math.abs(angle(approxPoints[i % 4], approxPoints[i - 2], approxPoints[i - 1]));
            Log.i(TAG, String.valueOf(cosine));
            maxCosine = Math.max(cosine, maxCosine);
        }
        return maxCosine;
    }

    private static double angle(Point p1, Point p2, Point p0) {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private static Point[] sortPoints(Point[] src) {
        ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));
        Point[] result = {null, null, null, null};

        Comparator<Point> sumComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
            }
        };

        Comparator<Point> diffComparator = new Comparator<Point>() {

            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
            }
        };

        // top-left corner = minimal sum
        result[0] = Collections.min(srcPoints, sumComparator);
        // bottom-right corner = maximal sum
        result[2] = Collections.max(srcPoints, sumComparator);
        // top-right corner = minimal difference
        result[1] = Collections.min(srcPoints, diffComparator);
        // bottom-left corner = maximal difference
        result[3] = Collections.max(srcPoints, diffComparator);

        return result;
    }

    private static List<MatOfPoint> findLargestContour(Mat inputMat) {
        Mat mHierarchy = new Mat();
        List<MatOfPoint> mContourList = new ArrayList<>();
        //finding contours
        Imgproc.findContours(inputMat, mContourList, mHierarchy, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);

        Mat mContoursMat = new Mat();
        mContoursMat.create(inputMat.rows(), inputMat.cols(), CvType.CV_8U);

        if (mContourList.size() != 0) {
            Collections.sort(mContourList, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return Double.valueOf(Imgproc.contourArea(rhs)).compareTo(Imgproc.contourArea(lhs));
                }
            });
            return mContourList;
        }
        return null;
    }

    private static Quadrilateral findQuadrilateral(List<MatOfPoint> mContourList) {
        for (MatOfPoint c : mContourList) {
            MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
            double peri = Imgproc.arcLength(c2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);
            Point[] points = approx.toArray();
            // select biggest 4 angles polygon
            if (approx.rows() == 4) {
                Point[] foundPoints = sortPoints(points);
                return new Quadrilateral(approx, foundPoints);
            }
        }
        return null;
    }

    private static PointF[] getOrderedPoints(PointF[] points) {

        PointF centerPoint = new PointF();
        int size = points.length;
        for (PointF pointF : points) {
            centerPoint.x += pointF.x / size;
            centerPoint.y += pointF.y / size;
        }

        PointF[] orderedPoints = new PointF[size];
        for (PointF pointF : points) {
            int index = -1;
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0;
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1;
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2;
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3;
            }
            orderedPoints[index] = pointF;
        }
        return orderedPoints;
    }

    private static SparseArray<PointF> getOrderedPointsAsMap(float[] points) {
        SparseArray<PointF> orderedPoints = new SparseArray<>();
        float[] centerPoint = new float[2];
        int size = 4;
        for (int i=0; i<4; i++) {
            centerPoint[0] += points[i] / size;
            centerPoint[1] += points[i+4] / size;
        }

        for (int i=0;i<4;i++) {
            int index = -1;
            if (points[i] < centerPoint[0] && points[i+4] < centerPoint[1]) {
                index = 0;
            } else if (points[i] > centerPoint[0] && points[i+4] < centerPoint[1]) {
                index = 1;
            } else if (points[i]< centerPoint[0] && points[i + 4] > centerPoint[1]) {
                index = 2;
            } else if (points[i]> centerPoint[0] && points[i + 4] > centerPoint[1]) {
                index = 3;
            }
            orderedPoints.put(index,new PointF(points[i],points[i+4]));
        }
        return orderedPoints;
    }

    private static float[] getOrderedPoints(float[] points) {
        float[] orderedPoints = new float[8];

        float[] centerPoint = new float[2];
        int size = 4;
        for (int i=0; i<4; i++) {
            centerPoint[0] += points[i] / size;
            centerPoint[1] += points[i+4] / size;
        }

        for (int i=0;i<4;i++) {
            int index = -1;
            if (points[i] < centerPoint[0] && points[i+4] < centerPoint[1]) {
                index = 0;
            } else if (points[i] > centerPoint[0] && points[i+4] < centerPoint[1]) {
                index = 1;
            } else if (points[i]< centerPoint[0] && points[i + 4] > centerPoint[1]) {
                index = 2;
            } else if (points[i]> centerPoint[0] && points[i + 4] > centerPoint[1]) {
                index = 3;
            }
            orderedPoints[index] = points[i];
            orderedPoints[index+4] = points[i+4];
        }
        return orderedPoints;
    }

    public static Bitmap enhanceReceipt(Bitmap image, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        int resultWidth = (int) (topRight.x - topLeft.x);
        int bottomWidth = (int) (bottomRight.x - bottomLeft.x);
        if (bottomWidth > resultWidth)
            resultWidth = bottomWidth;

        int resultHeight = (int) (bottomLeft.y - topLeft.y);
        int bottomHeight = (int) (bottomRight.y - topRight.y);
        if (bottomHeight > resultHeight)
            resultHeight = bottomHeight;

        Mat inputMat = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(image, inputMat);
        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);

        List<Point> source = new ArrayList<>();
        source.add(topLeft);
        source.add(topRight);
        source.add(bottomLeft);
        source.add(bottomRight);
        Mat startM = Converters.vector_Point2f_to_Mat(source);

        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(resultWidth, 0);
        Point ocvPOut3 = new Point(0, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, resultHeight);
        List<Point> dest = new ArrayList<>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight));

        Bitmap output = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    public static String[] saveToInternalMemory(Bitmap bitmap, String mFileDirectory, String
            mFileName, Context mContext, int mQuality) {

        String[] mReturnParams = new String[2];
        File mDirectory = getBaseDirectoryFromPathString(mFileDirectory, mContext);
        File mPath = new File(mDirectory, mFileName);
        try {
            FileOutputStream mFileOutputStream = new FileOutputStream(mPath);
            //Compress method used on the Bitmap object to write  image to output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, mQuality, mFileOutputStream);
            mFileOutputStream.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        mReturnParams[0] = mDirectory.getAbsolutePath();
        mReturnParams[1] = mFileName;
        return mReturnParams;
    }

    private static File getBaseDirectoryFromPathString(String mPath, Context mContext) {

        ContextWrapper mContextWrapper = new ContextWrapper(mContext);

        return mContextWrapper.getDir(mPath, Context.MODE_PRIVATE);
    }

    public static Bitmap decodeBitmapFromFile(String path, String imageName) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(new File(path, imageName).getAbsolutePath(),
                options);
    }

    /*
     * This method converts the dp value to px
     * @param context context
     * @param dp value in dp
     * @return px value
     */
    public static int dp2px(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return Math.round(px);
    }


    public static Bitmap decodeBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        // Raw height and width of image
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Deprecated
    public static Bitmap loadEfficientBitmap(byte[] data, int width, int height) {
        Bitmap bmp;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bmp;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }

            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Bitmap resizeToScreenContentSize(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static ArrayList<PointF> getPolygonDefaultPoints(Bitmap bitmap) {
        ArrayList<PointF> points;
        points = new ArrayList<>();
        points.add(new PointF(bitmap.getWidth() * (0.14f), (float) bitmap.getHeight() * (0.13f)));
        points.add(new PointF(bitmap.getWidth() * (0.84f), (float) bitmap.getHeight() * (0.13f)));
        points.add(new PointF(bitmap.getWidth() * (0.14f), (float) bitmap.getHeight() * (0.83f)));
        points.add(new PointF(bitmap.getWidth() * (0.84f), (float) bitmap.getHeight() * (0.83f)));
        return points;
    }

    public static boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    public static Quadrilateral detectLargestQuadrilateral(int width, int height, byte[] data) {
        Mat yuv = new Mat(new Size(width, height * 1.5), CV_8UC1);
        yuv.put(0, 0, data);

        Mat mat = new Mat(new Size(width, height), CV_8UC4);
        Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2BGR_NV21, 4);
        yuv.release();

        Size originalPreviewSize = mat.size();
        int originalPreviewArea = mat.rows() * mat.cols();

        Quadrilateral largestQuad = ScanUtils.detectLargestQuadrilateral(mat);
        mat.release();
        return largestQuad;
    }

    public static float[] detectEdges(int width, int height, byte[] data) {
        Mat yuv = new Mat(new Size(width, height * 1.5), CV_8UC1);
        yuv.put(0, 0, data);

        Mat mat = new Mat(new Size(width, height), CV_8UC4);
        Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2BGR_NV21, 4);
        yuv.release();

        Size originalPreviewSize = mat.size();
        int originalPreviewArea = mat.rows() * mat.cols();

        Quadrilateral largestQuad = ScanUtils.detectLargestQuadrilateral(mat);
        mat.release();
        if(largestQuad!=null) {
            Point[] p = largestQuad.points;
            float[] point = new float[8];
            point[0] = (float) p[0].x;
            point[1] = (float) p[1].x;
            point[2] = (float) p[2].x;
            point[3] = (float) p[3].x;

            point[4] = (float) p[0].x;
            point[5] = (float) p[1].x;
            point[6] = (float) p[2].x;
            point[7] = (float) p[3].x;

            return point;
        }
        return null;
    }

    public static Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        Point p1 = new Point(x1,y1);
        Point p2 = new Point(x2,y2);
        Point p3 = new Point(x3,y3);
        Point p4 = new Point(x4,y4);
        return enhanceReceipt(bitmap,p1,p2,p3,p4);
    }

    public static Bitmap getGrayBitmap(Bitmap b) {
        Mat tmp = new Mat (b.getWidth(), b.getHeight(), CvType.CV_8UC1);
        Mat resultMat = new Mat();
        Utils.bitmapToMat(b, tmp);
        Imgproc.cvtColor(tmp, resultMat, Imgproc.COLOR_RGB2GRAY);
        Bitmap result= Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat, result);
        return result;
    }

    public static Bitmap getMagicColorBitmap(Bitmap b) {
        Mat src = new Mat (b.getWidth(), b.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(b, src);
        Mat dst = src.clone();

        float alpha = 1.9f;
        float beta = -80f;
        dst.convertTo(dst,-1,alpha,beta);
        Bitmap result= Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst,result);
        return result;
    }

    public static Bitmap getBWBitmap(Bitmap b) {
        Mat tmp = new Mat (b.getWidth(), b.getHeight(), CvType.CV_8UC1);
        Mat resultMat = new Mat();
        Utils.bitmapToMat(b, tmp);
        Imgproc.cvtColor(tmp, resultMat, Imgproc.COLOR_RGB2GRAY);
        threshold(resultMat,resultMat,0,255,THRESH_BINARY | THRESH_OTSU);

        Bitmap result= Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat,result);
        return result;
    }
    public static Mat crop(Mat src, int left, int top, int width, int height) {
        return new Mat(src, new Rect(left,top,width,height));
    }

    public static BitmapDocument buildBitmapDocument(RawBitmapDocument rawDocument) {
        Bitmap rotatedBitmap;
        if(rawDocument.mRotateDegree!=0) {
            rotatedBitmap = Util.rotateBitmap(rawDocument.mOriginalBitmap, rawDocument.mRotateDegree);
        } else rotatedBitmap = rawDocument.mOriginalBitmap;
        Bitmap croppedBitmap = Util.centerCropBitmap(rotatedBitmap,rawDocument.mViewPort);
        Bitmap result = croppedBitmap;//Util.resizeBitmap(croppedBitmap,1080);

        float xRatio = (float) result.getWidth() / rawDocument.mViewPort[0];
        float yRatio = (float) result.getHeight() / rawDocument.mViewPort[1];

        float w = result.getWidth();
        float h = result.getHeight();
      /*
        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;*/

        //  Util.getDefaultValue(mEdgePoints);
        //    if(!Util.isEdgeValid(mEdgePoints))
        SparseArray<PointF> orderedPointF = getOrderedPointsAsMap(rawDocument.mEdgePoints);
        float x1 = w*orderedPointF.get(0).x;
        float x2 = w*orderedPointF.get(1).x;
        float x3 = w*orderedPointF.get(2).x;
        float x4 = w*orderedPointF.get(3).x;

        float y1 = h*orderedPointF.get(0).y;
        float y2 = h*orderedPointF.get(1).y;
        float y3 = h*orderedPointF.get(2).y;
        float y4 = h*orderedPointF.get(3).y;

        Bitmap documentBitmap = ScanUtils.getScannedBitmap(result, x1,y1,x2,y2,x3,y3,x4,y4);
        BitmapDocument document = new BitmapDocument();
        System.arraycopy(rawDocument.mEdgePoints,0,document.mEdgePoints,0,8);
        document.mOriginalBitmap = result;
        document.mDocumentBitmap = documentBitmap;
        return document;
    }

    public static void convertToPercent(float[] points, float w, float h) {
        if(points==null||points.length!=8) return;
        points[0]/=w;
        points[1]/=w;
        points[2]/=w;
        points[3]/=w;

        points[4]/=h;
        points[5]/=h;
        points[6]/=h;
        points[7]/=h;
    }

    public static float[] detectEdge(Bitmap bitmap, int angle) {
        float[] edge = new float[8];

        try {
            Mat mat = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
            Utils.bitmapToMat(bitmap, mat);

            Size originalPreviewSize = mat.size();
            int originalPreviewArea = mat.rows() * mat.cols();

            Quadrilateral largestQuad = ScanUtils.detectLargestQuadrilateral(mat);

            if (null == largestQuad) {
                Util.getDefaultValue(edge);
                return edge;
            }

            float previewWidth = (float) originalPreviewSize.width;
            float previewHeight = (float) originalPreviewSize.height;
            double area = Math.abs(Imgproc.contourArea(largestQuad.contour));
            Point[] p = largestQuad.points;
            //Height calculated on Y axis
            double resultHeight = p[1].y- p[0].y;
            double bottomHeight = p[3].y - p[2].y;
            if (bottomHeight > resultHeight)
                resultHeight = bottomHeight;

            //Width calculated on X axis
            double resultWidth = p[3].x - p[0].x;
            double bottomWidth = p[2].x - p[1].x;
            if (bottomWidth > resultWidth)
                resultWidth = bottomWidth;
            ImageDetectionProperties imgDetectionPropsObj
                    = new ImageDetectionProperties(previewWidth, previewHeight, resultWidth, resultHeight,
                    originalPreviewArea, area, p[0], p[1], p[2], p[3]);

            if (imgDetectionPropsObj.isDetectedAreaBeyondLimits()) {
                Util.getDefaultValue(edge);
                Log.d(TAG, "detectEdge: beyond limits");
                return edge;
            }

            edge[0] = (float) p[0].x;
            edge[1] = (float) p[1].x;
            edge[2] = (float) p[2].x;
            edge[3] = (float) p[3].x;

            edge[4] = (float) p[0].y;
            edge[5] = (float) p[1].y;
            edge[6] = (float) p[2].y;
            edge[7] = (float) p[3].y;
            Util.logPoints(TAG,edge);

            convertToPercent(edge,previewWidth,previewHeight);
            if(angle!=0)
                rotatePoint(edge,0.5f,0.5f, angle);
            Log.d(TAG, "detectEdge: available result");
            return edge;

        } catch (Exception e) {
            Util.getDefaultValue(edge);
            Log.d(TAG, "detectEdge: exception");
            return edge;
        }
    }

    /**
     *  centerCrop các điểm thuộc một hình chữ nhật
     *  trả về kích cỡ hình chữ nhật mới
     *  Lưu ý: Hàm này chỉ center crop chứ không resize các điểm để đồng bộ với hình chữ nhật  tham số
     * @param pixelPoint các điểm cũ
     * @param wSrc độ rộng hình chữ nhật cũ
     * @param hSrc độ cao hình chữ nhật
     * @param wViewPort độ rộng hình chữ nhật để tính tỷ lệ
     * @param hViewPort độ cao hình chữ nhật để tính tỷ lệ
     * @return độ rộng và độ cao mới (ít nhất một trong hai là giá trị của hình chữ nhật cũ)
     */
    public static float[] centerCropPoint(float[] pixelPoint, float wSrc, float hSrc, float wViewPort, float hViewPort) {
        float wPerHDest = wViewPort/hViewPort;
        float wPerHSrc = wSrc/hSrc;
        if(wPerHSrc<wPerHDest) {
            // bị cắt đi chiều cao, giữ nguyên chiều rộng
            float hDest = wSrc/wPerHDest;
            //result = Bitmap.createBitmap(bitmap,0,(int)(hOrg/2 - hDest/2),(int)wOrg,(int)hDest);

            float newTop = hSrc/2 - hDest/2;
            pixelPoint[4] -= newTop;
            pixelPoint[5] -= newTop;
            pixelPoint[6] -= newTop;
            pixelPoint[7] -= newTop;
            return new float[] {wSrc,hDest};
        } else {
            // bị cắt theo chiều rộng, giữ nguyên chiều cao
            float wDest = hSrc*wPerHDest;
            //result = Bitmap.createBitmap(bitmap,(int)(wOrg/2 - wDest/2),0,(int)wDest,(int)hOrg);

            float newLeft = wSrc/2 - wDest/2;
            pixelPoint[0] -= newLeft;
            pixelPoint[1] -= newLeft;
            pixelPoint[2] -= newLeft;
            pixelPoint[3] -= newLeft;
            return new float[] {wDest, hSrc};
        }
    }

    public static void rotatePoint(float[] points, float centerX, float centerY, float degreeAngle) {
        // anticlockwise rotate
        double radAngle = Math.toRadians(degreeAngle);
        float s = (float) Math.sin(radAngle);
        float c = (float) Math.cos(radAngle);
        for (int i = 0; i < 4; i++) {
            points[i] -= centerX;
            points[i+4] -= centerY;

            float xnew = points[i]*c- points[i+4]*s;
            float ynew = points[i]*s + points[i+4]*c;

            points[i] = xnew + centerX;
            points[i+4] = ynew + centerY;
        }
    }

    public static void rotatePoint2(float[] points, float centerX, float centerY, float degreeAngle) {

    }

    public static PointF rotatePoint(float cx, float cy, float angleInRad, PointF p) {
        float s = (float) Math.sin(angleInRad);
        float c = (float) Math.cos(angleInRad);

        // translate point back to origin:
        p.x -= cx;
        p.y -= cy;

        // rotate point
        float xnew = p.x * c - p.y * s;
        float ynew = p.x * s + p.y * c;

        // translate point back:
        p.x = xnew + cx;
        p.y = ynew + cy;
        return p;
    }

    public static void scalePoint(float[] pixelPoint, float scaleX, float scaleY) {
        pixelPoint[0] *=scaleX;
        pixelPoint[1] *=scaleX;
        pixelPoint[2] *=scaleX;
        pixelPoint[3] *=scaleX;

        pixelPoint[4] *=scaleY;
        pixelPoint[5] *=scaleY;
        pixelPoint[6] *=scaleY;
        pixelPoint[7] *=scaleY;

    }

    private static String getRealPathFromURI(Context context, Uri contentURI) {
        String filePath;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }

    public static int getOrientation(Context context, Uri photoUri)
    {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        return orientation;
    }

    public static int getCameraPhotoOrientation(Context context, Uri uri) {
        int rotate = 0;
        try {
            //context.getContentResolver().notifyChange(imageUri, null);

            ExifInterface ei;
            if (Build.VERSION.SDK_INT > 23) {
                InputStream input = context.getContentResolver().openInputStream(uri);
                ei = new ExifInterface(input);
            }
            else
                ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static void boundToViewPort(float[] points, float[] viewPort) {
        for (int i = 0; i <4; i++) {
            if(points[i] < 0) points[i] = 0;
            else if(points[i] > viewPort[0]) points[i] = viewPort[0];

            if(points[i+4]<0) points[i+4] = 0;
            else if(points[i+4] > viewPort[1]) points[i+4] = viewPort[1];
        }
    }
}
