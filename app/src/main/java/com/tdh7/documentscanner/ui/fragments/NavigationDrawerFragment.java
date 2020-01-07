package com.tdh7.documentscanner.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;

import com.ldt.navigation.NavigationFragment;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

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
        return inflater.inflate(R.layout.navigation_drawer,container,false);
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

    @OnClick(R.id.pref_section)
    void openPreferences() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).closeDrawerIfOpened();
            mAppIcon.postDelayed((() ->
                    ((MainActivity) activity).presentFragment(PreferencesFragment.newInstance())),350);
        }
    }

    private  MainFragment getMainFragment() {
        return ((MainActivity) Objects.requireNonNull(getActivity())).getMainFragment();
    }

    @OnClick(R.id.switch_one)
    void switchOne() {
        if(getActivity()!=null)
        getActivity().setTheme(R.style.ThemeDark);
    }

    @OnClick(R.id.switch_two)
    void switchTwo() {
        if(getActivity()!=null)
            getActivity().setTheme(R.style.ThemeLight);
    }
    @OnClick(R.id.switch_three)
    void switchThree() {

    }

    @OnClick(R.id.switch_four)
    void switchFour() {

    }
}
