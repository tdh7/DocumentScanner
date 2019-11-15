package com.tdh7.documentscanner.ui;
import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.fragments.MainFragment;
import com.tdh7.documentscanner.ui.permissionscreen.PermissionActivity;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;

import androidx.core.content.ContextCompat;

public class MainActivity extends PermissionActivity {
    public static final String TAG ="MainActivity";
    private static final String mOpenCvLibrary = "opencv_java3";

    public static final String PACKAGE_NAME = "com.tdh7.documentscanner.";
    public static final String ACTION_PERMISSION_START_UP = PACKAGE_NAME + "permission_start_up";
    public static final String ACTION_OPEN_CAMERA_PICKER = PACKAGE_NAME + "open_camera_picker";
    public static final String ACTION_OPEN_MEDIA_PICKER =    PACKAGE_NAME + "open_media_picker";


    MainFragment mMainFragment;
    public void openSystemCamera() {
        if(mMainFragment!=null) mMainFragment.openSystemCamera();
    }

    static {
        System.loadLibrary(mOpenCvLibrary);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        mMainFragment = MainFragment.newInstance();
        initNavigation(savedInstanceState,R.id.container,mMainFragment);
        boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        if(hasPermission) {
            new Handler().post(this::openCamera);
        }
    }
    public void openCamera() {
        presentFragment(CameraPickerFragment.newInstance());
    }

    public void reloadSavedList() {
        executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
    }
    public void closeDrawerIfOpened() {
        if(mMainFragment!=null)
        mMainFragment.closeDrawerIfOpened();
    }

    public void setTheme(boolean white) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!white) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
              //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().setNavigationBarColor(Color.BLACK);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setNavigationBarColor(Color.WHITE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(Intent intent, int permissionType, boolean granted) {
        if(intent!=null) {
            String action = intent.getAction();
            if(action!=null)
            switch (action) {
                case ACTION_PERMISSION_START_UP:
                    if(permissionType==PermissionActivity.PERMISSION_STORAGE && granted)
                    //refreshData();
                        mMainFragment.refreshData();
                    break;
                case ACTION_OPEN_CAMERA_PICKER:
                    if(permissionType==PermissionActivity.PERMISSION_CAMERA && granted) {
                        presentFragment(new CameraPickerFragment());
                    }
                case ACTION_OPEN_MEDIA_PICKER:
                    if(permissionType==PermissionActivity.PERMISSION_STORAGE&&granted)
                        mMainFragment.openMediaGallery();
            }
        }
    }

}
