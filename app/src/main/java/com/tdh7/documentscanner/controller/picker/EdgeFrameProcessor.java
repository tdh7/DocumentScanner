package com.tdh7.documentscanner.controller.picker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.renderscript.RenderScript;

import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.util.ImageDetectionProperties;
import com.tdh7.documentscanner.util.Quadrilateral;
import com.tdh7.documentscanner.util.RenderScriptHelper;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;

import static org.opencv.core.CvType.CV_8UC1;

public class EdgeFrameProcessor implements FrameProcessor {
    private static final String TAG = "EdgeFrameProcessor";

    private final WeakReference<CameraPickerFragment> mWeakFragment;

    public int getRotateDegree() {
        return mRotateDegree;
    }

    public void setRotateDegree(int rotateDegree) {
        mRotateDegree = rotateDegree;
    }
    private void toast(@StringRes int s) {
        CameraPickerFragment fragment = mWeakFragment.get();
        if(fragment!=null) fragment.toast(s);
    }

    private void toast(String s) {
        CameraPickerFragment fragment = mWeakFragment.get();
        if(fragment!=null) fragment.toast(s);
    }
    private int mRotateDegree= 0;
    private RenderScript mRenderScript;
  /*  public ScanComponent getScanComponent() {
        return mScanComponent;
    }*/

    public boolean isActiveProcessor() {
        return mActiveProcessor;
    }

    public void setActiveProcessor(boolean activeProcessor) {
        mActiveProcessor = activeProcessor;
    }

    public boolean mActiveProcessor = true;

    public AutoCapturer getAutoCapturer() {
        return mAutoCapturer;
    }

    private AutoCapturer mAutoCapturer;

    /*  private ScanComponent mScanComponent = new ScanComponent();*/
    public void destroy() {
        if(mAutoCapturer!=null)
            mAutoCapturer.destroy();
        mWeakFragment.clear();
        //mScanComponent = null;
        mRenderScript = null;
    }

    public void activeAutoCapture() {
        if(mAutoCapturer!=null) mAutoCapturer.activeAutoCapture();
    }

    public void disableAutoCapturer() {
        if(mAutoCapturer!=null) mAutoCapturer.disable();
    }

    public EdgeFrameProcessor(@NonNull Context context, @NonNull CameraPickerFragment fragment) {
        mWeakFragment = new WeakReference<>(fragment);
        mRenderScript = RenderScript.create(context);
        mAutoCapturer = new AutoCapturer(fragment);
    }

    /*   private SparseArray<PointF> getEdgePoints( MarkerView view,float[] points, int w, int h) {
           List<PointF> pointFs = getContourEdgePoints(points,(float) view.getWidth()/w,(float) view.getHeight()/h);
           SparseArray<PointF> orderedPoints = orderedValidEdgePoints(view, pointFs,w,h);
           return orderedPoints;
       }*/
    private  static SparseArray<PointF> getOutlinePoints(int width, int height) {
        SparseArray<PointF> outlinePoints = new SparseArray<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(width, 0));
        outlinePoints.put(2, new PointF(0, height));
        outlinePoints.put(3, new PointF(width, height));
        return outlinePoints;
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

    private List<PointF> getContourEdgePoints(float[] points, float widthRatio, float heightRatio, float rotateDegree) {

        float x1 = points[0]*widthRatio;
        float x2 = points[1]*widthRatio;
        float x3 = points[2]*widthRatio;
        float x4 = points[3]*widthRatio;

        float y1 = points[4]*heightRatio;
        float y2 = points[5]*heightRatio;
        float y3 = points[6]*heightRatio;
        float y4 = points[7]*heightRatio;

        PointF p0 = new PointF(points[0]*widthRatio,points[4]*heightRatio);
        PointF p1 = new PointF(points[1]*widthRatio,points[5]*heightRatio);
        PointF p2 = new PointF(points[2]*widthRatio,points[6]*heightRatio);
        PointF p3 = new PointF(points[3]*widthRatio,points[7]*heightRatio);

        List<PointF> pointFs = new ArrayList<>();
        float rad = (float) Math.toRadians(- rotateDegree);

        pointFs.add(rotatePoint(0.5f,0.5f,rad,p0));
        pointFs.add(rotatePoint(0.5f,0.5f,rad,p1));
        pointFs.add(rotatePoint(0.5f,0.5f,rad,p2));
        pointFs.add(rotatePoint(0.5f,0.5f,rad,p3));

        return pointFs;
    }

    @Override
    public void process(@NonNull Frame frame) {

        // do nothing if processor is inactive
        mRotateDegree = frame.getRotation();
        if(!isActiveProcessor()) return;
        CameraPickerFragment cpf = mWeakFragment.get();

        // do nothing if no camera picker
        if(cpf==null) return;

        try {
            Resolution size = frame.getSize();
            Mat yuv = new Mat(new Size(size.width, size.height * 1.5), CV_8UC1);
            yuv.put(0, 0, frame.getImage());

            Mat mat = new Mat(new Size(size.width, size.height), CvType.CV_8UC4);
            Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2BGR_NV21, 4);
            yuv.release();

            Size originalPreviewSize = mat.size();
            int originalPreviewArea = mat.rows() * mat.cols();

            Quadrilateral largestQuad = ScanUtils.detectLargestQuadrilateral(mat);
            //clearAndInvalidateCanvas();

            mat.release();

            if (null != largestQuad) {
                findLargestRect(largestQuad.contour, largestQuad.points, originalPreviewSize, originalPreviewArea);
            } else {
                //toast("Finding rect");
            }
        } catch (Exception e) {
            //toast("Finding rect with an exception");
        }
    }

    private void findLargestRect(MatOfPoint2f approx, Point[] p, Size stdSize, int previewArea) {
        CameraPickerFragment cpf = mWeakFragment.get();
        if(cpf == null) return;

        // ATTENTION: axis are swapped
        float previewWidth = (float) stdSize.height;
        float previewHeight = (float) stdSize.width;

        Log.i(TAG, "previewWidth: " + String.valueOf(previewWidth));
        Log.i(TAG, "previewHeight: " + String.valueOf(previewHeight));

        //Points are drawn in anticlockwise direction

        /*
        path.moveTo(previewWidth - (float) points[0].y, (float) points[0].x);
        path.lineTo(previewWidth - (float) points[1].y, (float) points[1].x);
        path.lineTo(previewWidth - (float) points[2].y, (float) points[2].x);
        path.lineTo(previewWidth - (float) points[3].y, (float) points[3].x);
        */
        float[] points = new float[8];
        double area = Math.abs(Imgproc.contourArea(approx));
        Log.i(TAG, "Contour Area: " + String.valueOf(area));


        //Height calculated on Y axis
        double resultHeight = p[1].x - p[0].x;
        double bottomHeight = p[2].x - p[3].x;
        if (bottomHeight > resultHeight)
            resultHeight = bottomHeight;

        //Width calculated on X axis
        double resultWidth = p[3].y - p[0].y;
        double bottomWidth = p[2].y - p[1].y;
        if (bottomWidth > resultWidth)
            resultWidth = bottomWidth;

        Log.i(TAG, "resultWidth: " + String.valueOf(resultWidth));
        Log.i(TAG, "resultHeight: " + String.valueOf(resultHeight));

        ImageDetectionProperties imgDetectionPropsObj
                = new ImageDetectionProperties(previewWidth, previewHeight, resultWidth, resultHeight,
                previewArea, area, p[0], p[1], p[2], p[3]);


        if (imgDetectionPropsObj.isDetectedAreaBeyondLimits()) {
            //toast("Finding rect");
            Util.getDefaultValue(points);
            mAutoCapturer.onProcess(points);
            cpf.setPoints(points);
            return;
        }

        points[0] = (float) previewWidth - (float) p[0].y;
        points[1] = (float) previewWidth - (float) p[1].y;
        points[2] = (float) previewWidth - (float) p[2].y;
        points[3] = (float) previewWidth - (float) p[3].y;

        points[4] = (float) p[0].x;
        points[5] = (float) p[1].x;
        points[6] = (float) p[2].x;
        points[7] = (float) p[3].x;
        float[] viewPort = cpf.getViewPort();
        float[] centerCropSize = ScanUtils.centerCropPoint(points,previewWidth,previewHeight,viewPort[0],viewPort[1]);
        ScanUtils.scalePoint(points,viewPort[0]/centerCropSize[0],viewPort[1]/centerCropSize[1]);
        ScanUtils.convertToPercent(points,viewPort[0],viewPort[1]);
        mAutoCapturer.onProcess(points);
        cpf.setPoints(points);

        if (imgDetectionPropsObj.isDetectedAreaBelowLimits()) {

            if (imgDetectionPropsObj.isEdgeTouching()) {
                //toast("Move away");
            } else {
                //toast("Move closer");
            }
        } else if (imgDetectionPropsObj.isDetectedHeightAboveLimit()) {
            //toast("Move away");
        } else if (imgDetectionPropsObj.isDetectedWidthAboveLimit() || imgDetectionPropsObj.isDetectedAreaAboveLimit()) {
            //toast("Move away");
        } else {
            if (imgDetectionPropsObj.isEdgeTouching()) {
                //toast("Move away");
            } else if (imgDetectionPropsObj.isAngleNotCorrect(approx)) {
                //toast("Adjust angle");
            } else {
                Log.i(TAG, "GREEN" + "(resultWidth/resultHeight) > 4: " + (resultWidth / resultHeight) +
                        " points[0].x == 0 && points[3].x == 0: " + p[0].x + ": " + p[3].x +
                        " points[2].x == previewHeight && points[1].x == previewHeight: " + p[2].x + ": " + p[1].x +
                        "previewHeight: " + previewHeight);
                //toast("capturing");

            /*    if (!isAutoCaptureScheduled) {
                    scheduleAutoCapture(scanHint);
                }*/
            }
        }
        Log.i(TAG, "Preview Area 95%: " + 0.95 * previewArea +
                " Preview Area 20%: " + 0.20 * previewArea +
                " Area: " + String.valueOf(area));
    }

    public void _process(@NonNull Frame frame) {
        if(!isActiveProcessor()) return;
        CameraPickerFragment cpf = mWeakFragment.get();
        if(cpf!=null) {
            long start = System.currentTimeMillis();
            byte[] byteArray = frame.getImage();
            Resolution resolution = frame.getSize();
            mRotateDegree = frame.getRotation();
            long tick1 = System.currentTimeMillis();
            Bitmap frameBitmap = RenderScriptHelper.convertYuvToRgbIntrinsic(mRenderScript,byteArray,resolution.width,resolution.height);
            Bitmap resizedBitmap;
            if(Math.max(frameBitmap.getWidth(),frameBitmap.getHeight())>768) {
                resizedBitmap = Util.resizeBitmap(frameBitmap, 768);
                frameBitmap.recycle();
            }
            else resizedBitmap = frameBitmap;

            Bitmap rotatedBitmap;
            if(mRotateDegree!=0) {
                rotatedBitmap = Util.rotateBitmap(resizedBitmap, - mRotateDegree);
                resizedBitmap.recycle();
            } else rotatedBitmap = resizedBitmap;
            Bitmap croppedBitmap = Util.centerCropBitmap(rotatedBitmap,cpf.getViewPort());
            rotatedBitmap.recycle();
            long tick2 = System.currentTimeMillis();
            cpf.setPreview(croppedBitmap, frame.getRotation());

            Log.d(TAG, "process: image size = "+ frame.getSize().width+"x"+frame.getSize().height+", rotation = "+frame.getRotation());

            //if(mScanComponent==null) return;
            float[] points = null;//mScanComponent.getPoints(croppedBitmap);
            if(points==null) return;
            ScanUtils.convertToPercent(points,croppedBitmap.getWidth(),croppedBitmap.getHeight());
            mAutoCapturer.onProcess(points);
            cpf.setPoints(points);
                /*if(points!=null) {
                    StringBuilder pbuilder = new StringBuilder("Points detected : ");
                    for (int i = 0; i < points.length; i++) {
                        pbuilder.append(points[i]).append(", ");
                    }
                    Log.d(TAG, pbuilder.toString());
                    long tick3 = System.currentTimeMillis();
                    List<PointF> pf = getContourEdgePoints(points,1f/size.width,1f/size.height,0);
                    long tick4 = System.currentTimeMillis();
                    Log.d(TAG, "process: tick1 = "+(tick1-start)+", tick2 = "+ (tick2-tick1)+", tick3 = "+(tick3-tick2)+", tick4 = "+(tick4-tick3));
                    if(pf!=null) {
                        StringBuilder builder = new StringBuilder("Marker detected : ");
                        for (int i = 0; i < pf.size(); i++) {
                            builder.append(pf.get(i).x).append('x').append(pf.get(i).y).append(", ");
                        }
                        Log.d(TAG, builder.toString());
                    } else Log.d(TAG, "process: point is null");
                    cpf.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            cpf.mMarkerView.setPoints(pf);

                        }
                    });
                }*/

        }
    }


}