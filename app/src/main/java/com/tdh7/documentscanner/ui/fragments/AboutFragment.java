package com.tdh7.documentscanner.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.PresentStyle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tdh7.documentscanner.R;

public class AboutFragment extends BaseFragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public int contentLayout() {
        return R.layout.about_layout;
    }

 /*   @Override
    public int defaultTransition() {
        return PresentStyle.ROTATE_DOWN_LEFT;
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }

    @OnClick(R.id.back_button)
    void back() {dismiss();}

    @BindView(R.id.back_button)
    View mBack;

    @Override
    public void onSetStatusBarMargin(int value) {
        ((ViewGroup.MarginLayoutParams)mBack.getLayoutParams()).topMargin += value;
    }
}
