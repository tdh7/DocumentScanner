package com.tdh7.documentscanner.ui.permissionscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ldt.navigation.ui.NavigationActivity;

import java.util.ArrayList;

public abstract class PermissionActivity extends NavigationActivity implements PermissionRequestDialog.RequestResultCallback {
    private static final String TAG = "PermissionActivity";

    public static final int PERMISSION_STORAGE = 1;
    public static final int PERMISSION_CAMERA = 2;
    public static final int PERMISSION_ALL = 3;
    public static final int PERMISSION_READY = 0;
    private static final String PERMISSION_RESULT = "permission_result";

    private int mCurrentPermissionRequest = -1;
    private Intent mRequestIntent = null;
    private boolean mPermissionShown = false;

    public final void executePermissionAction(Intent intent, int permission) {
        if(intent!=null) {
            mCurrentPermissionRequest = permission;
            mRequestIntent = intent;
            requestPermission();
        }
    }

    public final void executePermissionActionWithCallback(PermissionSessionRequestCallback callback, Intent intent, int permission) {

    }

    public final void executeWriteStorageAction(Intent intent) {
        if(intent==null) return;
        mCurrentPermissionRequest = PERMISSION_STORAGE;
        mRequestIntent = intent;
        requestPermission();
    }

    public void onRequestPermissionsResult(Intent intent, int permissionType, boolean granted) {
    }

    private void onRequestPermissionsResult( int permissionType, boolean granted, boolean keepIntent) {
        if(mRequestIntent!=null) {
            Intent intent = mRequestIntent;
            if(!keepIntent)
            mRequestIntent = null;
            onRequestPermissionsResult(intent, permissionType, granted);
        }
    }

    private void onRequestPermissionsResult(int permissionType, boolean granted) {
        if(mRequestIntent!=null) {
            Intent intent = mRequestIntent;
                mRequestIntent = null;
            onRequestPermissionsResult(intent, permissionType, granted);
        }
    }

    public final int fixSuitablePermission() {
        switch (mCurrentPermissionRequest) {
            case PERMISSION_STORAGE:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) return PERMISSION_READY;
                else return mCurrentPermissionRequest;
            case PERMISSION_CAMERA:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) return PERMISSION_READY;
                else return mCurrentPermissionRequest;
            case PERMISSION_ALL:
                boolean storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                boolean camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
               if(storage && camera) {
                   return PERMISSION_READY;
               } else if(storage) {
                   return PERMISSION_CAMERA;
               } else if(camera) {
                   return PERMISSION_STORAGE;
               } else return PERMISSION_ALL;
            default:
                return PERMISSION_ALL;
        }
    }

    public final void requestPermission() {
        int fixedPermission = fixSuitablePermission();
        if (fixedPermission!=PERMISSION_READY){

            if (!mPermissionShown || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // show explaination dialog and wait user accept then show permission dialog
                PermissionRequestDialog.newInstance(fixedPermission).setRequestResultCallback(this).show(getSupportFragmentManager(),PermissionRequestDialog.TAG);
            } else {
                // immediately show permission dialog
                onRequestDialogResult(mCurrentPermissionRequest,true);
            }
        } else
            // permission already granted
            onRequestPermissionsResult( mCurrentPermissionRequest,new String [] {"",""}, new int[] {PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED} );
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
        switch (requestCode) {
            case PERMISSION_STORAGE:
                if(permissions.length >0&& grantResult.length>0)
                        onRequestPermissionsResult(PERMISSION_STORAGE,grantResult[0]==PackageManager.PERMISSION_GRANTED);
                break;
            case PERMISSION_CAMERA:
                if(permissions.length >0 && grantResult.length>0)
                     onRequestPermissionsResult(PERMISSION_CAMERA,grantResult[0]==PackageManager.PERMISSION_GRANTED);
                break;
            case PERMISSION_ALL:
                if(permissions.length>=2&&grantResult.length >= 2) {
                    boolean storage = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResult[1] == PackageManager.PERMISSION_GRANTED;
                    if (storage && camera) {
                        onRequestPermissionsResult(PERMISSION_STORAGE, true,true);
                        onRequestPermissionsResult(PERMISSION_CAMERA, true);
                    }
                    else if(storage) {
                        onRequestPermissionsResult(PERMISSION_STORAGE, true,true);
                        onRequestPermissionsResult(PERMISSION_CAMERA,false);
                    }
                    else if(camera) {
                        onRequestPermissionsResult(PERMISSION_STORAGE,false,true);
                        onRequestPermissionsResult(PERMISSION_CAMERA, true);
                    }
                    else {
                        onRequestPermissionsResult(PERMISSION_STORAGE,false,true);
                        onRequestPermissionsResult(PERMISSION_CAMERA,false);
                    }
                }
                break;
        }
    }

    @Override
    public final void onRequestDialogResult(int permissionType , boolean result) {
        if(result) {
            String[] permissions = null;
            switch (mCurrentPermissionRequest) {
                case PERMISSION_STORAGE:
                        permissions = new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                };
                    break;
                case PERMISSION_CAMERA:
                    permissions = new String[] {
                            Manifest.permission.CAMERA
                    };
                case PERMISSION_ALL:
                    permissions = new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    };
                default:
                    break;
            }
            if(permissions!=null) {
                mPermissionShown = true;
                ActivityCompat.requestPermissions(this,permissions,permissionType);
            }
        } else {
            Log.d(TAG, "false result");
        }
    }

    public interface PermissionSessionRequestCallback {
        void onRequestPermissionsResult(Intent intent, int permissionType, boolean granted);
    }

    public interface PermissionCallback {
        public void onReceivePermissionResult(int permission, boolean result);
    }

    public ArrayList<PermissionCallback> mPermissionCallbacks = new ArrayList<>();
    public void addPermissionCallback(PermissionCallback callback) {
        if(!mPermissionCallbacks.contains(callback))
            mPermissionCallbacks.add(callback);
    }
    public void removePermissionCallback(PermissionCallback callback) {
        mPermissionCallbacks.remove(callback);
    }
}