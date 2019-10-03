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
import com.tdh7.documentscanner.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.Configuration;
import io.fotoapparat.parameter.AntiBandingMode;
import io.fotoapparat.parameter.ScaleType;
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
        return inflater.inflate(R.layout.camera_picker_layout,container,false);
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

    private void init() {
         CameraConfiguration mCameraConfiguration = CameraConfiguration
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
                .previewScaleType(ScaleType.CenterInside)
                .lensPosition(back())
                .build();
        mFotoapparat.updateConfiguration(mCameraConfiguration);
    }

    Configuration mConfiguration;

    private Fotoapparat mFotoapparat;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;

   @BindView(R.id.camera_view)
   CameraView mCameraView;

   @BindView(R.id.focusView)
    FocusView mFocusView;

 /*   @Override
    public int defaultTransition() {
        return PresentStyle.STACK_LEFT;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeCamera();
    }

    private void resumeCamera() {
        //mCameraKitView.onResume();
    }

    @Override
    public void onPause() {
        //mCameraKitView.onPause();
        super.onPause();
    }

    public interface CameraPickerListener {
        boolean onNewCapture(Bitmap bmp);
    }
}
