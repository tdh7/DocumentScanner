package com.tdh7.documentscanner.ui.taker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationFragment;
import com.scanlibrary.ScanComponent;
import com.tdh7.documentscanner.R;

import org.jetbrains.annotations.NotNull;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.Configuration;
import io.fotoapparat.parameter.AntiBandingMode;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
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
              //  .frameProcessor(new SampleFrameProcessor())
                .build();
        mFotoapparat = Fotoapparat
                .with(getContext())
                .into(mCameraView)
                .focusView(mFocusView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .build();
        mFotoapparat.updateConfiguration(mCameraConfiguration);
    }

    private Fotoapparat mFotoapparat;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;

   @BindView(R.id.camera_view)
   CameraView mCameraView;

   @BindView(R.id.focusView)
    FocusView mFocusView;

   @BindView(R.id.root)
   View mRoot;

   ScanComponent mScanComponent = new ScanComponent();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public interface CameraPickerListener {
        boolean onNewCapture(Bitmap bmp);
    }

    private class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void process(@NonNull Frame frame) {
            // Perform frame processing, if needed

        }
    }
}
