package com.tdh7.documentscanner.ui;
import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.ldt.navigation.FragNavigationController;
import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.ThemeStyle;
import com.tdh7.documentscanner.controller.session.DocumentSession;
import com.tdh7.documentscanner.ui.fragments.BaseFragment;
import com.tdh7.documentscanner.ui.fragments.MainFragment;
import com.tdh7.documentscanner.ui.permissionscreen.PermissionActivity;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class MainActivity extends PermissionActivity {
    public static final String TAG ="MainActivity";
    private static final String mOpenCvLibrary = "opencv_java3";

    public static final String PACKAGE_NAME = "com.tdh7.documentscanner.";
    public static final String ACTION_PERMISSION_START_UP = PACKAGE_NAME + "permission_start_up";
    public static final String ACTION_OPEN_CAMERA_PICKER = PACKAGE_NAME + "open_camera_picker";
    public static final String ACTION_OPEN_MEDIA_PICKER =    PACKAGE_NAME + "open_media_picker";
    MainFragment mMainFragment;

    public MainFragment getMainFragment() {
    return mMainFragment;
    }

    @Override
    public void setTheme(int resId) {
        super.setTheme(resId);
        if(R.style.ThemeDark== resId) {
            getWindow().setBackgroundDrawableResource(R.color.backColorDark);
            ThemeStyle.init(this,false);
        } else {
            getWindow().setBackgroundDrawableResource(android.R.color.white);
            ThemeStyle.init(this,true);
        }
        updateNavigationControllerTheme();
    }

    public void updateNavigationControllerTheme() {
        if(mMainFragment==null) return;
        FragNavigationController controller = mMainFragment.getNavigationController();
        if(controller!=null) {
            int count = controller.getFragmentCount();
            for(int i =0;i<count;i++) {
                BaseFragment fragment = (BaseFragment) controller.getFragmentAt(i);
                if(fragment!=null) fragment.updateTheme();
            }
        }
    }

    public void openSystemCamera() {
        if(mMainFragment!=null) mMainFragment.openSystemCamera();
    }

    static {
        System.loadLibrary(mOpenCvLibrary);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitTheme();
        setContentView(R.layout.main_activity_layout);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        mMainFragment = MainFragment.newInstance();
        initNavigation(savedInstanceState,R.id.container,mMainFragment);

        DocumentSession.init();
        boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        if(hasPermission) {
            new Handler().post(this::openCamera);
        }

    }

    public void InitTheme() {
        ThemeStyle.init(this, applySystemTheme());
    }

    public boolean applySystemTheme() {
        int themeOption = AppCompatDelegate.getDefaultNightMode();
        switch (themeOption) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                setTheme(R.style.ThemeDark);
                return false;
            
            case AppCompatDelegate.MODE_NIGHT_NO:
                setTheme(R.style.ThemeLight);
                return true;

            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                int themeMode = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
                switch (themeMode) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.ThemeDark);
                        return false;

                    case Configuration.UI_MODE_NIGHT_NO:
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    default:
                        setTheme(R.style.ThemeLight);
                        return true;
                }    
        }
    }

    @Override
    protected void onDestroy() {
        ThemeStyle.destroy();
        DocumentSession.destroy();
        super.onDestroy();
    }

    public void openCamera() {
         try {
             presentFragment(CameraPickerFragment.newInstance());
         }catch (Exception e) {
             Log.d(TAG,"exception when open camera: "+e.getMessage());
         }
    }

    public void reloadSavedList() {
        executeWriteStorageAction(new Intent(ACTION_PERMISSION_START_UP));
    }
    public void closeDrawerIfOpened() {
        if(mMainFragment!=null)
        mMainFragment.closeDrawerIfOpened();
    }

    public void setTheme(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!light) {
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
