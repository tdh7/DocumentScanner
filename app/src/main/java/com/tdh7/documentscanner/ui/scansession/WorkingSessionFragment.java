package com.tdh7.documentscanner.ui.scansession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.R;

import butterknife.BindDimen;

public class WorkingSessionFragment extends NavigationFragment {
    private static final String TAG = "WorkingSessionFragment";

    private float mStatusBarHeight = 0;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBarHeight = value;
    }



}
