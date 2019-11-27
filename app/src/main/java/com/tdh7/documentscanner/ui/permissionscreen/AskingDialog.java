package com.tdh7.documentscanner.ui.permissionscreen;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tdh7.documentscanner.R;

import java.util.ArrayList;

public class AskingDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AskingDialog";
    public static final int RESULT_TWO = 2;
    public static final int RESULT_ONE = 1;
    public static final int RESULT_CANCEL = 0;

    public static AskingDialog newInstance() {
        return new AskingDialog();
    }
    public AskingDialog setStringButton(int buttonOne, int buttonTwo) {
        return this;
    }

    @Override
    public int getTheme() {
        return R.style.DialogDimDisabled;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ask_dialog_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind(view);
        bindContent();
    }

    private void bindContent() {

    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(this, TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private TextView mTitle;
    private TextView mDescription;

    private void bind(View root) {
     View closeButton = root.findViewById(R.id.close);
     View buttonTwo = root.findViewById(R.id.button_two);
     View buttonOne = root.findViewById(R.id.button_one);
     mTitle = root.findViewById(R.id.permission_title);
     mDescription = root.findViewById(R.id.permission_description);

     if(closeButton!=null) closeButton.setOnClickListener(this);
        if(buttonOne!=null) buttonOne.setOnClickListener(this);
        if(buttonTwo!=null) buttonTwo.setOnClickListener(this);
    }


    public interface AskingResultCallback {
        void onDialogResult(int result);
    }

    public AskingDialog setCallback(AskingResultCallback callback) {
        mCallback = callback;
        return this;
    }

    private AskingResultCallback mCallback;

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mCallback !=null) mCallback.onDialogResult(RESULT_CANCEL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.button_one:
                if(mCallback!=null)
                    mCallback.onDialogResult(RESULT_ONE);
                mCallback = null;
                dismiss();
            case R.id.button_two:
                if(mCallback!=null) mCallback.onDialogResult(RESULT_TWO);
                mCallback = null;
                dismiss();
                break;

        }
    }
}