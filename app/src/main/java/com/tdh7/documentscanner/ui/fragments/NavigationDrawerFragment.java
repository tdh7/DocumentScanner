package com.tdh7.documentscanner.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationDrawerFragment extends Fragment {

    public static NavigationDrawerFragment newInstance() {

        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        return fragment;
    }

    @BindView(R.id.app_icon) View mAppIcon;
    @BindView(R.id.app_title) View mAppTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_drawer_view,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        ((ViewGroup.MarginLayoutParams)mAppTitle.getLayoutParams()).topMargin += NavigationFragment.getStatusHeight(getResources());

    }

    @OnClick(R.id.about_section)
    void openAbout() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).closeDrawerIfOpened();
            mAppIcon.postDelayed((() ->
            ((MainActivity) activity).presentFragment(AboutFragment.newInstance())),350);
        }
    }

}
