package com.tdh7.documentscanner.ui.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.widget.custom.DmcConstraintLayout;

public class SearchFragment extends BaseFragment {

    @Override
    public int contentLayout() {
        return R.layout.search_layout;
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.FADE;
    }

    Unbinder mUnbinder;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mUnbinder!=null) mUnbinder.unbind();
        mUnbinder = ButterKnife.bind(this,view);
    }

    @OnClick(R.id.back_button)
    void back() {dismiss();}
    @BindView(R.id.back_button) View mBackButton;

    @Override
    public void onSetStatusBarMargin(int value) {
        ((ViewGroup.MarginLayoutParams)mBackButton.getLayoutParams()).topMargin+=value;
    }

    @BindView(R.id.root)
    DmcConstraintLayout mLayout;

    @OnClick(R.id.switch_one)
    void doSwitchOne() {
        Toasty.success(App.getInstance(),"switch dark").show();
        getActivity().setTheme(R.style.ThemeDark);
    }

    public void recreateView() {
        Scene scene =  Scene.getSceneForLayout((ViewGroup) getContentView(), R.layout.search_layout, getContext());
        TransitionManager.go(scene, new Fade().setDuration(125));

        //View v = onCreateView(LayoutInflater.from(getContext()),getRootLayout());
        //FrameLayout f = getRootLayout();
        //if(v!=null)
        onViewCreated(getContentView(),null);
        onSetStatusBarMargin(getStatusHeight(getResources()));
        //f.removeAllViews();
      //  TransitionManager.beginDelayedTransition(f);
        //f.addView(v);
       // TransitionManager.endTransitions(f);
    }

    @OnClick(R.id.switch_two)
    void doSwitchTwo() {
        Toasty.success(App.getInstance(),"switch light").show();
        getActivity().setTheme(R.style.ThemeLight);
    }
}
