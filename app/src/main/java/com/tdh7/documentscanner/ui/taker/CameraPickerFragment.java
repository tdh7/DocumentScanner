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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.renderscript.RenderScript;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.scanlibrary.PolygonView;
import com.scanlibrary.ScanComponent;
import com.scanlibrary.ScannerActivity;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.scansession.WorkingSessionFragment;
import com.tdh7.documentscanner.ui.widget.CaptureView;
import com.tdh7.documentscanner.ui.widget.MarkerView;
import com.tdh7.documentscanner.util.PreferenceUtil;
import com.tdh7.documentscanner.util.RenderScriptHelper;
import com.tdh7.documentscanner.util.Tool;
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
import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.selector.AntiBandingModeSelectorsKt;
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

public class CameraPickerFragment extends NavigationFragment implements CaptureView.CaptureListener {
    private static final String TAG = "CameraPickerFragment";
    public static final int PERMISSION_CAMERA = 1;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.camera_picker_layout,container,false);
        return root;
    }

    private View mPermissionView = null;

    private void bindPermissionScreen() {
        boolean hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
        if(hasPermission) {
            removePermissionScreenIfAny();
            mFotoapparat.start();
        }
        else showPermissionScreen();
    }

    private void showPermissionScreen() {
        if(getContext()!=null&&mPermissionView==null) {
         mPermissionView = LayoutInflater.from(getContext()).inflate(R.layout.camera_ask_permission,mRoot,false);
        View button = mPermissionView.findViewById(R.id.button);
        if(button!=null) button.setOnClickListener(this::allowAccess);
         mRoot.addView(mPermissionView);
        }
    }

    private void allowAccess(View ignored) {
        requestPermissions(new String[] {
                Manifest.permission.CAMERA},PERMISSION_CAMERA);
    }



    private void removePermissionScreenIfAny() {
        if(getContext()!=null&&mPermissionView!=null) {
            mPermissionView.findViewById(R.id.button).setOnClickListener(null);
            mRoot.removeView(mPermissionView);
            mPermissionView = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        bindPermissionScreen();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        init();
        restoreCaptureMode();
    }

    private void restoreCaptureMode() {
        mCaptureMode = PreferenceUtil.getInstance().getSavedCaptureMode();
        if(mCaptureMode==CaptureView.MODE_AUTO_CAPTURE) {
            mCaptureIcon.setTranslationY(63*mDpUnit);
            mCaptureIcon.setScaleX(0.4f);
            mCaptureIcon.setScaleY(0.4f);
            mAutoCaptureButton.setTextColor(getResources().getColor(R.color.flatOrange));
            mManualCaptureButton.setTextColor(getResources().getColor(R.color.flatWhite));
            mAutoCaptureButton.setTranslationX(0);
            mManualCaptureButton.setTranslationX(0);
            mCaptureIcon.setCaptureMode(mCaptureMode);
        }
        mCaptureIcon.setCaptureListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindPermissionScreen();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFotoapparat.stop();
    }

    @Override
    public int defaultDuration() {
        return 150;
    }

    private CameraConfiguration mCameraConfiguration;

    private void init() {
        if(getContext()!=null) {
            mEdgeFrameProcessor = new EdgeFrameProcessor(getContext(), this);
        }
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
        ((ViewGroup.MarginLayoutParams)mStatusView.getLayoutParams()).height = value;

       int height = Tool.getScreenSize(getContext())[1];
       int fragmentHeight = getActivity().findViewById(R.id.container).getHeight();

       if(fragmentHeight<height) {
           // this means a navigation bar existed
           ((ViewGroup.MarginLayoutParams)mCameraCardView.getLayoutParams()).topMargin = value;
       } else {
           // do nothing
       }
    }

    private EdgeFrameProcessor mEdgeFrameProcessor ;

    private Fotoapparat mFotoapparat;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;

    @BindView(R.id.status_bar)
    View mStatusView;


    @BindView(R.id.camera_view)
    CameraView mCameraView;

   @BindView(R.id.focusView)
    FocusView mFocusView;

   @BindView(R.id.root)
   ViewGroup mRoot;

   @BindView(R.id.camera_card_view)
   View mCameraCardView;

   @BindView(R.id.capture_icon)
   CaptureView mCaptureIcon;

   private int mCaptureMode = CaptureView.MODE_AUTO_CAPTURE;

   @BindView(R.id.auto_capture_text)
   TextView mAutoCaptureButton;

   @BindView(R.id.manual_text)
   TextView mManualCaptureButton;

    @OnClick(R.id.manual_text)
    void switchToManualCapture() {

       if(mCaptureMode!=CaptureView.MODE_MANUAL_CAPTURE) {
           mCaptureMode = CaptureView.MODE_MANUAL_CAPTURE;
           PreferenceUtil.getInstance().setSavedOriginal3DPhoto(mCaptureMode);
           mCaptureIcon.setCaptureMode(mCaptureMode);

           mCaptureIcon.animate().scaleX(1f).scaleY(1f).translationY(0).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
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
   void switchToAutoCapture() {
       if(mCaptureMode!=CaptureView.MODE_AUTO_CAPTURE) {
           mCaptureMode = CaptureView.MODE_AUTO_CAPTURE;
           PreferenceUtil.getInstance().setSavedOriginal3DPhoto(mCaptureMode);
           mCaptureIcon.setCaptureMode(mCaptureMode);
           mCaptureIcon.animate().scaleX(0.4f).scaleY(0.4f).translationY(63*mDpUnit).setInterpolator(new OvershootInterpolator())
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
               // mPreviewView.setRotation(-rotate);
            }
        });
    }

    public float[] getViewPort() {
        return mMarkerView.getViewPort();
    }

    public void setPoints(float[] points) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMarkerView.setPoints(points);
            }
        });
    }

    @Override
    public boolean onNewCapture() {

        PhotoResult result = mFotoapparat.takePicture();
        result.toBitmap().whenDone(mBitmapPhotoResultListener);
      //  mFotoapparat.stop();

        return true;
    }

    private WhenDoneListener<BitmapPhoto> mBitmapPhotoResultListener = new WhenDoneListener<BitmapPhoto>() {
        @Override
        public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
            mCaptureIcon.unlockCapture();
            if(bitmapPhoto!=null) {
                // Toasty.warning(App.getInstance(),"Bitmap is rotated by "+bitmapPhoto.rotationDegrees).show();
                presentFragment(WorkingSessionFragment.newInstance(bitmapPhoto));
            }
        }
    };

    private WhenDoneListener<Bitmap> mBitmapResultListener = new WhenDoneListener<Bitmap>() {
        @Override
        public void whenDone(@org.jetbrains.annotations.Nullable Bitmap bitmap) {
            mCaptureIcon.unlockCapture();
            if(bitmap!=null) {
                // Toasty.warning(App.getInstance(),"Bitmap is rotated by "+bitmapPhoto.rotationDegrees).show();
                presentFragment(WorkingSessionFragment.newInstance(bitmap));
            }
        }
    };

    @BindView(R.id.markerView)
    MarkerView mMarkerView;

    Handler mHandler = new Handler();
    public Handler getHandler() {
        return mHandler;
    }


}
