package com.tdh7.documentscanner.ui.permissionscreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tdh7.documentscanner.R;

public class PermissionRequestDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "PermissionRequestDialog";

    private int mPermissionType = PermissionActivity.PERMISSION_STORAGE;
    public PermissionRequestDialog setPermissionType(int type) {
        mPermissionType = type;
        return this;
    }

    public static PermissionRequestDialog newInstance(int permissionType) {
        return new PermissionRequestDialog().setPermissionType(permissionType);
    }

    @Override
    public int getTheme() {
        return R.style.DialogDimDisabled;
    }

    private boolean mResult = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.permission_request,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind(view);
        bindContent();
    }


    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(this, TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private TextView mPermissionTitle;
    private TextView mPermissionDescription;
    private ImageView mPermissionImage;

    private void bind(View root) {
     View closeButton = root.findViewById(R.id.close);
     View okButton = root.findViewById(R.id.button_two);
     mPermissionImage = root.findViewById(R.id.permission_image);
     mPermissionTitle = root.findViewById(R.id.permission_title);
     mPermissionDescription = root.findViewById(R.id.permission_description);

     if(closeButton!=null) closeButton.setOnClickListener(this);
     if(okButton!=null) okButton.setOnClickListener(this);
    }

    public void bindContent() {
        switch (mPermissionType) {
            case PermissionActivity.PERMISSION_ALL:
                mPermissionTitle.setText(R.string.app_require_permissions);
                mPermissionDescription.setText(R.string.permission_description);
                mPermissionImage.setImageResource(R.drawable.view_file);
                break;
            case PermissionActivity.PERMISSION_STORAGE:
                mPermissionTitle.setText(R.string.access_external_storage);
                mPermissionDescription.setText(R.string.storage_permission_description);
                mPermissionImage.setImageResource(R.drawable.document);
                break;
            case PermissionActivity.PERMISSION_CAMERA:
                mPermissionTitle.setText(R.string.access_camera);
                mPermissionDescription.setText(R.string.camera_permisison_description);
                mPermissionImage.setImageResource(R.drawable.camera);
                break;
            default:
                break;
        }
    }

    public interface RequestResultCallback {
        void onRequestDialogResult(int permissionType, boolean result);
    }

    public PermissionRequestDialog setRequestResultCallback(RequestResultCallback requestResultCallback) {
        mRequestResultCallback = requestResultCallback;
        return this;
    }

    private RequestResultCallback mRequestResultCallback;

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mRequestResultCallback !=null) mRequestResultCallback.onRequestDialogResult(mPermissionType,mResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                mResult = false;
                dismiss();
                break;
            case R.id.button_two:
                mResult = true;
                dismiss();
                break;

        }
    }
}