package com.tdh7.documentscanner.ui.picker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.ThemeStyle;
import com.tdh7.documentscanner.controller.picker.CropEdgeQuickView;
import com.tdh7.documentscanner.controller.picker.EdgeFrameProcessor;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.editor.DocumentEditorFragment;
import com.tdh7.documentscanner.ui.fragments.BaseFragment;
import com.tdh7.documentscanner.ui.fragments.EditorFragment;
import com.tdh7.documentscanner.ui.widget.CaptureIconView;
import com.tdh7.documentscanner.ui.widget.MarkerView;
import com.tdh7.documentscanner.util.PreferenceUtil;
import com.tdh7.documentscanner.util.Tool;
import com.tdh7.documentscanner.util.Util;

import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import es.dmoral.toasty.Toasty;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.on;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

public class CameraPickerFragment extends BaseFragment implements CaptureIconView.CaptureListener, CropEdgeQuickView.QuickViewCallback {
    private static final String TAG = "CameraPickerFragment";
    public static final int PERMISSION_CAMERA = 1;

    public static final int MODE_NEW_SESSION = 0;
    public static final int MODE_IMPORT = 1;
    public static final int CAPTURE_MODE_NONE = -1;

    private int mMode = MODE_NEW_SESSION;
    public static final int CAPTURE_MODE_AUTO_CAPTURE = 0;
    public static final int CAPTURE_MODE_MANUAL_CAPTURE = 1;

    ArrayList<RawBitmapDocument> mBitmapDocuments = new ArrayList<>();

    private CameraPickerResult mResultCallback;
    public static final int RESULT_DISCARD = 0;
    public static final int RESULT_OK = 1;
    public static final int RESULT_FAILURE = 2;

    private EdgeFrameProcessor mEdgeFrameProcessor ;
    private Fotoapparat mFotoapparat;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit;

    @BindView(R.id.status_bar)
    View mStatusView;


    @BindView(R.id.camera_view)
    public CameraView mCameraView;

    @BindView(R.id.focusView)
    FocusView mFocusView;

    @BindView(R.id.root)
    public
    ViewGroup mRoot;

    @BindView(R.id.camera_card_view)
    View mCameraCardView;

    @BindView(R.id.mode_mark_view) View mMark1;
    @BindView(R.id.mode_mark_view_2) View mMark2;
    @BindView(R.id.action_button) TextView mActionButton;

    /*
       CameraPicker chạy theo 2 mode :
    +. Tạo mới Session (Sau khi thoát picker sẽ tạo mới session và hiển thị nó
    +. Nhận vào một callback từ session, sau khi thoát picker sẽ trả kết quả cho session

     */

    public static CameraPickerFragment newInstance() {
        CameraPickerFragment fragment = new CameraPickerFragment();
        fragment.mMode = MODE_NEW_SESSION;
        return fragment;
    }

    public static CameraPickerFragment newInstance(CameraPickerResult result) {
        if(result==null) return newInstance();
        CameraPickerFragment fragment = new CameraPickerFragment();
        fragment.mResultCallback = result;
        fragment.mMode = MODE_IMPORT;
        return fragment;
    }


    public interface CameraPickerResult {
        void onCameraPickerComplete(int status, ArrayList<RawBitmapDocument> result);
    }

    @Override
    public int contentLayout() {
        return R.layout.camera_picker_layout;
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
        View button = mPermissionView.findViewById(R.id.button_two);
        if(button!=null) button.setOnClickListener(this::allowAccess);
         mRoot.addView(mPermissionView);
        }
    }

    private void allowAccess(View ignored) {
        requestPermissions(
                new String[] {Manifest.permission.CAMERA}
                ,PERMISSION_CAMERA);
    }

    @OnClick(R.id.system_camera_icon)
    void openSystemCamera() {
        Activity activity = getActivity();
        dismiss();
        if(activity instanceof MainActivity) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).openSystemCamera();
                }
            },350);
        }
    }

    @OnLongClick(R.id.system_camera_icon)
    void longClickSystemCamera() {
        Util.vibrate(App.getInstance());
        Toasty.normal(App.getInstance(),"Use the system camera").show();
    }

    private void removePermissionScreenIfAny() {
        if(getContext()!=null&&mPermissionView!=null) {
            mPermissionView.findViewById(R.id.button_two).setOnClickListener(null);
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
        mCaptureIcon.setCaptureListener(this);
        updateActionButton();
        switchCaptureMode(PreferenceUtil.getInstance().getSavedCaptureMode());
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

    @Override
    public void onDestroy() {
        mEdgeFrameProcessor.destroy();
        mEdgeFrameProcessor = null;
        mCropEdgeQuickView = null;
        super.onDestroy();
    }

    private void init() {
   /*      mCameraConfiguration = CameraConfiguration
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
                .build();*/
   if(getContext()==null) return;
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
    }

    @Override
    public boolean isWhiteTheme() {
        return false;
    }

    public float mStatusHeight = 0;
    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusHeight = value;
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

    public EdgeFrameProcessor getEdgeFrameProcessor() {
        return mEdgeFrameProcessor;
    }

   private void updateActionButton() {
       if(mMode==MODE_NEW_SESSION) {
           mActionButton.setText(getString(R.string.add_x, mBitmapDocuments.size()));
       } else {
           mActionButton.setText(getString(R.string.import_x, mBitmapDocuments.size()));
       }
       if(mBitmapDocuments.isEmpty()) {
           mActionButton.setTextColor(0x99F5F5F5);
           mActionButton.setBackgroundResource(R.drawable.background_inactive);
       } else {
           mActionButton.setTextColor(getResources().getColor(R.color.flatOrange));
           mActionButton.setBackgroundResource(R.drawable.background_active);
       }
   }

   @OnClick(R.id.action_button)
   void clickAction() {
       if(mBitmapDocuments.isEmpty()) return;
       if(mMode==MODE_NEW_SESSION) {

       } else {

       }
       if(mCropEdgeQuickView!=null) {
           mCropEdgeQuickView.getCurrentEdge(mBitmapDocuments.get(mBitmapDocuments.size()-1).mEdgePoints);
       }
       dismiss();
       mCaptureIcon.post(() ->
               presentFragment(DocumentEditorFragment.newInstance().addAll(mBitmapDocuments)));
   }

   @BindView(R.id.capture_icon)
   public CaptureIconView mCaptureIcon;

   private int mCaptureMode = CAPTURE_MODE_NONE;

    public static final int FLASH_AUTO = 0;
    public static final int FLASH_OFF = 1;
    public static final int FLASH_ON = 2;
    public static final int FLASH_TORCH = 3;

   private int mFlashMode = FLASH_AUTO;
    @OnClick(R.id.flash)
    void switchFlashMode() {
        int next;
        switch (mFlashMode) {
            case FLASH_AUTO:
                next = FLASH_ON;
                break;
            case FLASH_ON:
                next = FLASH_OFF;
                break;
            case FLASH_OFF:
                next = FLASH_TORCH;
                break;
            case FLASH_TORCH:
                next = FLASH_AUTO;
                break;
                default:
                    next = -1;
        }

        if(next!=-1) {
            mFlashMode = next;
            updateFlashMode();
        }
    }

    private void updateFlashMode() {
        UpdateConfiguration.Builder builder = new UpdateConfiguration.Builder();
        switch (mFlashMode) {
            case FLASH_AUTO:
                builder.flash(autoFlash());
                toast("Flash Auto");
                hideToast();
                break;
            case FLASH_ON:
                builder.flash(on());
                toast("Flash On");
                hideToast();
                break;
            case FLASH_OFF:
                builder.flash(off());
                toast("Flash Off");
                hideToast();
                break;
            case FLASH_TORCH:
                builder.flash(torch());
                toast("Flash Torch");
                hideToast();
                break;
        }
        mFotoapparat.updateConfiguration(builder.build());
    }

   @BindView(R.id.auto_capture_text)
   TextView mAutoCaptureButton;

   @BindView(R.id.manual_text)
   TextView mManualCaptureButton;

   public void switchCaptureMode(int mode) {
       if(mCaptureMode==mode||(mode!=CAPTURE_MODE_NONE&&mode!=CAPTURE_MODE_AUTO_CAPTURE&&mode!=CAPTURE_MODE_MANUAL_CAPTURE)) return;
       mCaptureMode = mode;
       PreferenceUtil.getInstance().setSavedCaptureMode(mCaptureMode);
       mCaptureIcon.setCaptureMode(mode);
       Util.vibrate(getContext());
       Util.vibrate(getContext());
       // animation
       switch (mode) {
           case CAPTURE_MODE_AUTO_CAPTURE:
               mToastTextView.animate().translationY(63*mDpUnit).start();
               mCaptureIcon.animate().scaleX(0.4f).scaleY(0.4f).translationY(63*mDpUnit).setInterpolator(new OvershootInterpolator())
                       .withEndAction(new Runnable() {
                           @Override
                           public void run() {
                               mAutoCaptureButton.setTextColor(getResources().getColor(R.color.flatOrange));
                               mManualCaptureButton.setTextColor(0x99F5F5F5);
                           }
                       }).start();
               mMark1.animate().alpha(1).start();
               mMark2.setAlpha(0);
               mAutoCaptureButton.animate().translationX(0).start();
               mManualCaptureButton.animate().translationX(0).start();
               mEdgeFrameProcessor.activeAutoCapture();
               break;
           case CAPTURE_MODE_MANUAL_CAPTURE:
               mToastTextView.animate().translationY(0).setInterpolator(new OvershootInterpolator()).start();
               mCaptureIcon.animate().scaleX(1f).scaleY(1f).translationY(0).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                   @Override
                   public void run() {
                       mManualCaptureButton.setTextColor(getResources().getColor(R.color.flatOrange));
                       mAutoCaptureButton.setTextColor(0x99F5F5F5);
                   }
               }).start();
               mMark2.animate().alpha(1).start();
               mMark1.setAlpha(0);
               mAutoCaptureButton.animate().translationX(24*mDpUnit).start();
               mManualCaptureButton.animate().translationX(-24*mDpUnit).start();
               mEdgeFrameProcessor.disableAutoCapturer();
               break;
       }
   }

    @OnClick(R.id.manual_text)
    void switchToManualCapture() {
       switchCaptureMode(CAPTURE_MODE_MANUAL_CAPTURE);
   }
    @OnClick(R.id.auto_capture_text)
   void switchToAutoCapture() {
       switchCaptureMode(CAPTURE_MODE_AUTO_CAPTURE);
   }

   @OnClick(R.id.back_button)
   void backClick() {
        if(getActivity()!=null)
        getActivity().onBackPressed();
   }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getContext()!=null) {
            mEdgeFrameProcessor = new EdgeFrameProcessor(getContext(), this);
        }
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }

    @Override
    public void onResume() {
        super.onResume();
        ThemeStyle.pushTheme(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ThemeStyle.popTheme();
    }

    @Override
    public boolean onBackPressed() {
        if(mCropEdgeQuickView!=null&&mCropEdgeQuickView.isAttached()) {
            mCropEdgeQuickView.detach();
            return false;
        } else return true;
    }

    @BindView(R.id.preview)
    ImageView mPreviewView;

    public void setPreview(Bitmap bitmap, float rotate) {
      /*  mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPreviewView.setImageBitmap(bitmap);
               // mPreviewView.setRotation(-rotate);
            }
        });*/
    }

    public float[] getViewPort() {
        return mMarkerView.getViewPort();
    }

    public void setPoints(float[] points) {
        mMarkerView.setPoints(points);
    }

    public void getPoints(float[] p) {
        mMarkerView.getPoints(p);
    }

    public void fireCaptureByAutoCapturer() {
        mHandler.post(mCaptureIcon::fireCaptureByAutoCapturer);
    }

    @Override
    public boolean onNewCapture() {
        mEdgeFrameProcessor.setActiveProcessor(false);
        mCaptureIcon.lockCapture();
        mFotoapparat.takePicture().toBitmap().whenDone(mBitmapPhotoResultListener);

        return true;
    }

    @Override
    public ViewGroup getParentLayout() {
        return mRoot;
    }

    @Override
    public void onQuickViewAttach() {
    }

    @Override
    public void onQuickViewDetach(float[] points) {
        mEdgeFrameProcessor.setActiveProcessor(true);
        if(mCaptureMode== CAPTURE_MODE_AUTO_CAPTURE)
            mEdgeFrameProcessor.activeAutoCapture();
        mCaptureIcon.unlockCapture();
        mCropEdgeQuickView = null;
    }

    private CropEdgeQuickView mCropEdgeQuickView;

    private void onCaptureBitmapAvailable(BitmapPhoto bitmapPhoto) {
        if(bitmapPhoto!=null) {
            // Toasty.warning(App.getInstance(),"Bitmap is rotated by "+bitmapPhoto.rotationDegrees).show();
            //  presentFragment(WorkingSessionFragment.newInstance(bitmapPhoto));
            int previewRotate = (360 - mEdgeFrameProcessor.getRotateDegree()) % 360 ;
            int captureRotate = (360 - bitmapPhoto.rotationDegrees) % 360;

            // preview 270 => rotate 360 - 270 = 90
            // capture 270 => rotate 360 - 270 = 90
            // mean that preview is the same as capture
            // => rotating 90 + 0 makes capture  shown as same as capture
            // rotate 90 + (90 - 90) => a + (b-a)
            // preview 270 => rotate 360 - 270 = 90
            // capture 0 => rotate 0
            // rotate 0 will make capture after the preview 90 degree
            //  => rotate 0 + 90 == 0 + (9- 0)

            RawBitmapDocument document =
                    new RawBitmapDocument(bitmapPhoto.bitmap,previewRotate,
                            getViewPort(),
                            getEdgeFrameProcessor().getAutoCapturer().getLatestEdges());
            mBitmapDocuments.add(document);
            mCaptureIcon.post(this::updateActionButton);
            mCaptureIcon.post(() -> {
                if(mCropEdgeQuickView==null) mCropEdgeQuickView = new CropEdgeQuickView();
                mCropEdgeQuickView.attachAndPresent(this,document);
            });
        }
    }

    private WhenDoneListener<BitmapPhoto> mBitmapPhotoResultListener = new WhenDoneListener<BitmapPhoto>() {
        @Override
        public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
          onCaptureBitmapAvailable(bitmapPhoto);
        }
    };

    @BindView(R.id.markerView)
    MarkerView mMarkerView;

    @BindView(R.id.toast_text_view)
    TextView mToastTextView;

    public void toast(final @StringRes int s) {
        mHandler.post(() -> {
            if(mToastTextView.getVisibility()== View.GONE)
                mToastTextView.animate().alpha(1)
                        .withStartAction(() -> {
                            mToastTextView.setText(s);
                            mToastTextView.setVisibility(View.VISIBLE);
                        }).setDuration(350).start();
            else
                mToastTextView.setText(s);
        });
    }

    public void toast(final String text) {
        mHandler.post(() -> {
            if(mToastTextView.getVisibility()== View.GONE)
                mToastTextView.animate().alpha(1)
                        .withStartAction(() -> {
                            mToastTextView.setText(text);
                            mToastTextView.setVisibility(View.VISIBLE);
                        }).setDuration(350).start();
            else
                mToastTextView.setText(text);
        });
    }

    public void hideToast() {
        mHandler.post(() -> {
            mToastTextView
                    .animate()
                    .alpha(0)
                    .withEndAction(() -> mToastTextView.setVisibility(View.GONE))
                    .setStartDelay(350)
                    .setDuration(350)
                    .start();
        });
    }

    Handler mHandler = new Handler();
    public Handler getHandler() {
        return mHandler;
    }


    public boolean isLockingCapture() {
        return mCaptureIcon.isLockingCapture();
    }
}
