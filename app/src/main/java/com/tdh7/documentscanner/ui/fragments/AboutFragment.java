package com.tdh7.documentscanner.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tdh7.documentscanner.R;

public class AboutFragment extends NavigationFragment {

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.about_layout,container,false);
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_UP;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }

    @OnClick(R.id.back_button)
    void back() {dismiss();}

}
