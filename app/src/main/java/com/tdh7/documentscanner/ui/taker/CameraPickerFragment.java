package com.tdh7.documentscanner.ui.taker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.renderscript.RenderScript;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.scanlibrary.PolygonView;
import com.scanlibrary.ScanComponent;
import com.scanlibrary.ScannerActivity;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.widget.MarkerView;
import com.tdh7.documentscanner.util.RenderScriptHelper;
import com.tdh7.documentscanner.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.Configuration;
import io.fotoapparat.parameter.AntiBandingMode;
import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.AntiBandingModeSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.autoRedEye;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

public class CameraPickerFragment extends NavigationFragment {
    private static final String TAG = "CameraPickerFragment";

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.camera_picker_layout,container,false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFotoapparat.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFotoapparat.stop();
    }

    CameraConfiguration mCameraConfiguration;

    private void init() {
        mEdgeFrameProcessor = new EdgeFrameProcessor(this);
         mCameraConfiguration = CameraConfiguration
                .builder()
                 .antiBandingMode(AntiBandingModeSelectorsKt.hz50())
                .photoResolution(standardRatio(
                        highestResolution()
                ))
                .focusMode(firstAvailable(
                        continuousFocusPicture(),
                        autoFocus(),
                        fixed()
                ))
                .flash(firstAvailable(
                        autoRedEye(),
                        autoFlash(),
                        torch(),
                        off()
                ))
                .previewFpsRange(highestFps())
                .sensorSensitivity(highestSensorSensitivity())
                .frameProcessor(mEdgeFrameProcessor)
                .build();
        mFotoapparat = Fotoapparat
                .with(getContext())
                .previewFpsRange(highestFps())
                .sensorSensitivity(highestSensorSensitivity())
                .frameProcessor(mEdgeFrameProcessor)
                .into(mCameraView)
                .focusView(mFocusView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .build();
        mFotoapparat.updateConfiguration(mCameraConfiguration);
    }

    @Override
    public boolean isWhiteTheme() {
        return false;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        ((ViewGroup.MarginLayoutParams)mStatusView.getLayoutParams()).topMargin =value;
    }

    private EdgeFrameProcessor mEdgeFrameProcessor ;

    private Fotoapparat mFotoapparat;

    public ScanComponent getScanComponent() {
        return mScanComponent;
    }

    private ScanComponent mScanComponent = new ScanComponent();

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;

    @BindView(R.id.status_bar)
    View mStatusView;

   @BindView(R.id.camera_view)
   CameraView mCameraView;

   @BindView(R.id.focusView)
    FocusView mFocusView;

   @BindView(R.id.root)
   View mRoot;

   @BindView(R.id.capture_icon)
   ImageView mCaptureIcon;

   private boolean mAutoCaptureMode = true;

   @BindView(R.id.auto_capture_text)
   TextView mAutoCaptureButton;

   @BindView(R.id.manual_text)
   TextView mManualCaptureButton;

    @OnClick(R.id.manual_text)
    void manualCapture() {
       if(mAutoCaptureMode) {
           mAutoCaptureMode =false;


           mCaptureIcon.animate().scaleX(1f).scaleY(1f).translationY(0).withEndAction(new Runnable() {
               @Override
               public void run() {
                   mManualCaptureButton.setTextColor(getResources().getColor(R.color.flatOrange));
                   mAutoCaptureButton.setTextColor(Color.WHITE);
               }
           }).start();

           mAutoCaptureButton.animate().translationX(24*mDpUnit).start();
           mManualCaptureButton.animate().translationX(-24*mDpUnit).start();
       }
   }
    @OnClick(R.id.auto_capture_text)
   void autoCapture() {
       if(!mAutoCaptureMode) {
           mAutoCaptureMode = true;
           mCaptureIcon.animate().scaleX(0.4f).scaleY(0.4f).translationY(63*mDpUnit)
                   .withEndAction(new Runnable() {
                       @Override
                       public void run() {
                           mAutoCaptureButton.setTextColor(getResources().getColor(R.color.flatOrange));
                           mManualCaptureButton.setTextColor(Color.WHITE);
                       }
                   }).start();

           mAutoCaptureButton.animate().translationX(0).start();
           mManualCaptureButton.animate().translationX(0).start();
       }
   }

   @OnClick(R.id.back_button)
   void backClick() {
        dismiss();
   }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }

    @Override
    public void onResume() {
        super.onResume();
      // mRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
         //       | View.SYSTEM_UI_FLAG_FULLSCREEN
      // );
       if(getActivity() instanceof MainActivity)
           ((MainActivity) getActivity()).setTheme(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setTheme(true);
    }

    @BindView(R.id.preview)
    ImageView mPreviewView;

    public void setPreview(Bitmap bitmap, float rotate) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPreviewView.setImageBitmap(bitmap);
                mPreviewView.setRotation(-rotate);
            }
        });
    }

    public interface CameraPickerListener {
        boolean onNewCapture(Bitmap bmp);
    }

    @BindView(R.id.markerView)
    MarkerView mMarkerView;

    Handler mHandler = new Handler();
    public Handler getHandler() {
        return mHandler;
    }

    private static class EdgeFrameProcessor implements FrameProcessor {
        private final WeakReference<CameraPickerFragment> mWeakFragment;
        private RenderScript mRenderScript;

        EdgeFrameProcessor(CameraPickerFragment fragment) {
            mWeakFragment = new WeakReference<>(fragment);
            mRenderScript = RenderScript.create(fragment.getContext());
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
        PointF rotatePoint(float cx,float cy,float angleInRad, PointF p) {
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
            CameraPickerFragment cpf = mWeakFragment.get();
            if(cpf!=null) {
                long start = System.currentTimeMillis();
                byte[] byteArray = frame.getImage();
                Resolution size = frame.getSize();
                frame.getRotation();
                long tick1 = System.currentTimeMillis();
                Bitmap bitmap = RenderScriptHelper.convertYuvToRgbIntrinsic(mRenderScript,byteArray,size.width,size.height);
                if(Math.max(size.width,size.height)>768) bitmap = Util.resizeBitmap(bitmap,768);
                size = new Resolution(bitmap.getWidth(),bitmap.getHeight());
                long tick2 = System.currentTimeMillis();
                cpf.setPreview(bitmap, frame.getRotation());

                Log.d(TAG, "process: image size = "+ frame.getSize().width+"x"+frame.getSize().height+", rotation = "+frame.getRotation());

                float[] points = cpf.getScanComponent().getPoints(bitmap);

                if(points!=null) {
                        StringBuilder pbuilder = new StringBuilder("Points detected : ");
                        for (int i = 0; i < points.length; i++) {
                            pbuilder.append(points[i]).append(", ");
                        }
                        Log.d(TAG, pbuilder.toString());
                        long tick3 = System.currentTimeMillis();
                   List<PointF> pf = getContourEdgePoints(points,1f/size.width,1f/size.height,frame.getRotation());
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
                }


            }
        }
    }
}
