package com.tdh7.documentscanner.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class OptionBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener{
    public final static String OPTION_RES_ARRAY = "option_res_array";
    public static final String TAG = "OptionBottomSheet";
    private int[] mOptionIDs;

    @Override
    public int getTheme() {
        return R.style.LowDimBottomSheetDialog;
    }

    public static OptionBottomSheet newInstance(int[] optionIDs, CallBack callback) {
        OptionBottomSheet fragment = new OptionBottomSheet();
        fragment.mOptionIDs = optionIDs;
        fragment.mCallBack = callback;
        return fragment;
    }

    public interface CallBack {
        boolean onOptionClicked(int optionID);
    }

    private CallBack mCallBack;

    @Override
    public void onDestroyView() {
        mCallBack = null;
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(),getTheme());
    }

    private ArrayList<View> mClickableViews = new ArrayList<>();
    private ArrayList<Integer> mClickableIDs = new ArrayList<>();

    public View initLayout(Context context, final int[] options) {
        if(context ==null) throw new NullPointerException();
        LinearLayout root = new LinearLayout(context);
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        float dp_8 = context.getResources().getDimension(R.dimen.dp_8);
        float dp = context.getResources().getDimension(R.dimen.dp_unit);
        root.setPadding((int)(dp*3),(int)(dp_8),(int)(dp*3),(int)(dp_8));
        root.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams((int)dp*35,(int)dp*5);
        imageParams.bottomMargin = (int)(5*dp);
        imageParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setImageResource(R.drawable.slide_up_down_icon_drawable);
        root.addView(imageView,imageParams);
        clearAllClickableViews();

        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48*dp));
        textViewParam.setMarginStart((int)(16*dp));
        textViewParam.setMarginEnd((int)(16*dp));

        int optionType = R.string.normal;

        for (int i = 0, optionsLength = options.length; i < optionsLength; i++) {

            if(options[i]==R.string.warning_divider) {
                optionType = R.string.warning_divider;
                View divider = new View(context);
                divider.setBackgroundColor(0x66cccccc);
                LinearLayout.LayoutParams divParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)dp);
                divParam.bottomMargin = (int)dp_8;
                divParam.topMargin = (int)(7*dp);
                root.addView(divider,divParam);
                continue;
            }
            else if(options[i]==R.string.dangerous_divider) {
                optionType = R.string.dangerous_divider;
                View divider = new View(context);
                divider.setBackgroundColor(0x66cccccc);
                LinearLayout.LayoutParams divParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)dp);
                divParam.bottomMargin = (int)dp_8;
                divParam.topMargin = (int)(7*dp);
                root.addView(divider,divParam);
                continue;
            } else if(options[i]==R.string.normal) {
                optionType = R.string.normal;
                continue;
            }
            else if(options[i]==R.string.focus_divider) {
                optionType = R.string.focus_divider;
                continue;
            }

            TextView view  = new TextView(context);
            view.setPadding((int)(12*dp),0,(int)(12*dp),0);

           /* if(options[i]==R.string.go_to_artist &&mObject instanceof Song && !((Song)mObject).artistName.isEmpty()) {
                view.setText(getString(R.string.see_more_about)+" "+ ((Song)mObject).artistName);
            } else*/
            view.setText(options[i]);

            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            view.setTypeface(Typeface.DEFAULT_BOLD);


            if(optionType==R.string.warning_divider)
                view.setTextColor(context.getResources().getColor(R.color.flatOrange));
            else if(optionType==R.string.dangerous_divider)
                view.setTextColor(context.getResources().getColor(R.color.tomato));
            else if(optionType==R.string.focus_divider)
                view.setTextColor(context.getResources().getColor(R.color.flatTealBlue));
            else
                view.setTextColor(context.getResources().getColor(android.R.color.black));

            view.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            view.setOnClickListener(this);
            addToBeRipple(R.drawable.ripple_effect,view);

            addClickableViews(view,options[i]);
            root.addView(view,textViewParam);
        }

        return root;
    }

    private void clearAllClickableViews() {
        mClickableViews.clear();
        mClickableIDs.clear();
    }

    private void addClickableViews(View view, int id) {
        mClickableViews.add(view);
        mClickableIDs.add(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initLayout(getContext(), mOptionIDs);
    }

    @Override
    public void onClick(View view) {
        if(view instanceof TextView) {
            int item = mClickableViews.indexOf(view);
            if (item != -1 && mCallBack != null) {
                if(mCallBack.onOptionClicked(mClickableIDs.get(item)))
                 this.dismiss();
            }
        }
    }

    private ArrayList<View> rippleViews = new ArrayList<>();
    private boolean first_time = true;
    public void addToBeRipple(int drawable,View... v) {
        if(first_time) {
            first_time = false;
            res = getResources();
        }
        int l = v.length;
        rippleViews.addAll(Arrays.asList(v));
        for(View view :v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(res.getDrawable(drawable));
            } else {
                //TODO: setBackground below Android L
            }
            view.setClickable(true);
        }
    }

    Resources res;
    public void applyRippleColor(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (final View v : rippleViews) {
                ((RippleDrawable) v.getBackground()).setColor(ColorStateList.valueOf(color));
            }
        }
        else {
            //TODO: setBackground below Android L
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(this, TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog != null) {
                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    behavior.setPeekHeight(-Util.getNavigationHeight(requireActivity()));
                    behavior.setHideable(false);
                    behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            if (newState == STATE_COLLAPSED)
                                OptionBottomSheet.this.dismiss();
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                        }
                    });
                }
            }
        });
    }
}