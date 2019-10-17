package com.tdh7.documentscanner.ui;
import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.fragments.MainFragment;
import com.tdh7.documentscanner.ui.permissionscreen.PermissionActivity;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;

public class MainActivity extends PermissionActivity {
    public static final String TAG ="MainActivity";

    public static final String ACTION_PERMISSION_START_UP = "permission_start_up";
    public static final String ACTION_OPEN_CAMERA_PICKER = "open_camera_picker";

    MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        mMainFragment = MainFragment.newInstance();
        initNavigation(savedInstanceState,R.id.container,mMainFragment);
       // new Handler().post(this::openCamera);
    }
    public void openCamera() {
        presentFragment(new CameraPickerFragment());
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
                    //showSavedList();
                    break;
                case ACTION_OPEN_CAMERA_PICKER:
                    if(permissionType==PermissionActivity.PERMISSION_CAMERA && granted) {
                        presentFragment(new CameraPickerFragment());
                    }
            }
        }
    }

}
