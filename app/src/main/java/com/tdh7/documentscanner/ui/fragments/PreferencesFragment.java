package com.tdh7.documentscanner.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.dialog.OptionBottomSheet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class PreferencesFragment extends BaseFragment {

    public static PreferencesFragment newInstance() {
        return new PreferencesFragment();
    }

    @Override
    public int contentLayout() {
        return R.layout.pref_layout;
    }

 /*   @Override
    public int defaultTransition() {
        return PresentStyle.ROTATE_DOWN_LEFT;
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        int current = AppCompatDelegate.getDefaultNightMode();
        switch (current) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                mCurrentOption = R.string.dark;
                break;

            case AppCompatDelegate.MODE_NIGHT_NO:
            mCurrentOption = R.string.light;
            break;

            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
            mCurrentOption = R.string.follow_system;
            break;
        }

        mAppThemeButton.setText(mCurrentOption);

    }
    @BindView(R.id.app_theme_button)
    TextView mAppThemeButton;

    @OnClick(R.id.back_button)
    void back() {dismiss();}

    @BindView(R.id.back_button)
    View mBack;

    @Override
    public void onSetStatusBarMargin(int value) {
        ((ViewGroup.MarginLayoutParams)mBack.getLayoutParams()).topMargin += value;
    }

    private int[] mAppThemeOptionIDs = new int[] {
            R.string.follow_system,
            R.string.light,
            R.string.dark
    };

    private OptionCallBack mOptionCallback = new OptionCallBack();

    private static class OptionCallBack implements OptionBottomSheet.CallBack {
        private PreferencesFragment mFragment;

        OptionBottomSheet.CallBack attach(PreferencesFragment fragment) {
            mFragment = fragment;
            return this;
        }

        void detach() {
            mFragment = null;
        }

        @Override
        public boolean onOptionClicked(int optionID) {
            if (mFragment != null && mFragment.mCurrentOption != optionID && mFragment.getActivity() != null) {
                mFragment.mCurrentOption = optionID;
                MainActivity activity = (MainActivity) mFragment.getActivity();
                switch (optionID) {
                    case R.string.follow_system:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        //activity.InitTheme();
                        //mFragment.getActivity().recreate();
                        activity.setTheme(R.style.ThemeLight);

                        detach();
                        break;
                    case R.string.light:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        //mFragment.getActivity().recreate();
                        //activity.InitTheme();
                        activity.setTheme(R.style.ThemeLight);

                        detach();
                        break;
                    case R.string.dark:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        //mFragment.getActivity().recreate();
                        activity.setTheme(R.style.ThemeDark);
                        //activity.InitTheme();
                        detach();
                        break;
                }
            }
            return true;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Toasty.normal(App.getInstance(),"new config received").show();
    }

    private int mCurrentOption;

    @OnClick({R.id.app_theme_button,R.id.app_theme_section})
    void showAppThemeOption() {
        if(getActivity() instanceof AppCompatActivity)
        OptionBottomSheet.newInstance(mAppThemeOptionIDs, mOptionCallback.attach(this))
                .show(getActivity().getSupportFragmentManager(), OptionBottomSheet.TAG);
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }
}
