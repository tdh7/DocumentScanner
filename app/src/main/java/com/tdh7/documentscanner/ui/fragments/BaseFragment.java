package com.tdh7.documentscanner.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.R;

public abstract class BaseFragment extends NavigationFragment {

    public abstract @LayoutRes int contentLayout();
    @Nullable
    @Override
    protected final View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(contentLayout(),container, false);
    }

    public void updateTheme() {
     //   Scene scene =  Scene.getSceneForLayout((ViewGroup) getContentView(), contentLayout(), getContext());
        //TransitionManager.go(scene, new Fade().setDuration(125));

        View v = onCreateView(LayoutInflater.from(getContext()),getRootLayout());
        FrameLayout f = getRootLayout();
        if(v!=null)
        onViewCreated(v,null);
        onSetStatusBarMargin(getStatusHeight(getResources()));
        f.removeAllViews();
        //  TransitionManager.beginDelayedTransition(f);
        f.addView(v);
        // TransitionManager.endTransitions(f);
    }
}
