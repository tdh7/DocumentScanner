package com.tdh7.documentscanner.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.fragments.AboutFragment;
import com.tdh7.documentscanner.ui.fragments.MainFragment;
import com.tdh7.documentscanner.ui.fragments.SearchFragment;
import com.tdh7.documentscanner.ui.permissionscreen.PermissionActivity;
import com.tdh7.documentscanner.ui.taker.CameraPickerFragment;

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
