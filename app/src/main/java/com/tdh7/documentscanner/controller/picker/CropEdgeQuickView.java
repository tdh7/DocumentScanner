package com.tdh7.documentscanner.controller.picker;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.filter.FilterImageView;
import com.tdh7.documentscanner.controller.filter.canvas.CanvasFilter;
import com.tdh7.documentscanner.controller.filter.canvas.QuickViewCanvasFilter;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.fragments.MainFragment;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.ui.widget.MarkerView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CropEdgeQuickView {
    private static final String TAG = "CropEdgeQuickView";
    private CameraPickerFragment mFragment;
    private ViewGroup mParentLayout;
    private View mLayout;

    private static final int MODE_INSIDE_MAIN_SCREEN = 0;
    private static final int MODE_INSIDE_CAMERA_PICKER = 1;


    public boolean isAttached() {
        return mLayout!=null;
    }

    @BindView(R.id.filter_image_view)
    FilterImageView mFilterImageView;

    @BindView(R.id.card_view)
    FrameLayout mCardView;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

    public CropEdgeQuickView() {

    }

    public void init(CameraPickerFragment fragment) {
        mFragment = fragment;
        mParentLayout = fragment.mRoot;
    }

    @BindView(R.id.status_bar)
    View mStatusView;

    @BindView(R.id.markerView)
    MarkerView mMarkerView;

    private float mStatusHeight = 0;

    public void getCurrentEdge(float[] point) {
        mMarkerView.getPoints(point);
    }
    private Unbinder mUnbinder;
    private RawBitmapDocument mBitmapDocument;
    public void attachAndPresent(RawBitmapDocument document) {
        if(mFragment==null||mParentLayout==null)
            return;

        int rotationDegrees = document.mRotateDegree;
        Bitmap bitmap = document.mOriginalBitmap;

        if(mLayout==null) {
            mLayout = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.crop_edge_quick_view, mParentLayout, false);
            mUnbinder = ButterKnife.bind(this,mLayout);
            mStatusHeight = mFragment.mStatusHeight;
            ((ViewGroup.MarginLayoutParams)mStatusView.getLayoutParams()).height = (int) mStatusHeight;
            ((ViewGroup.MarginLayoutParams)mCardView.getLayoutParams()).topMargin =    ((ViewGroup.MarginLayoutParams)mParentLayout.findViewById(R.id.camera_card_view).getLayoutParams()).topMargin;;
            mParentLayout.addView(mLayout);
            CanvasFilter canvasFilter = mFilterImageView.getFilter();
            Log.d(TAG, "attachAndPresent: image was rotated by "+rotationDegrees);
            if(canvasFilter instanceof QuickViewCanvasFilter)
                ((QuickViewCanvasFilter) canvasFilter).setRotateValue(rotationDegrees);
            mFilterImageView.setBitmap(bitmap);
            mLayout.setBackgroundColor(Color.BLACK);
            mCardView.animate().scaleX(0.78f).scaleY(0.78f).setDuration(550).setInterpolator(new OvershootInterpolator()).start();
            mMarkerView.animate().scaleX(0.78f).scaleY(0.78f).setDuration(550).setInterpolator(new OvershootInterpolator()).start();

            mMarkerView.setPoints(document.mEdgePoints);
            mFragment.onQuickViewAttach();
          // mLayout.setAlpha(0);
          //  mLayout.animate().alpha(1f).setDuration(250).start();
           /* ObjectAnimator.ofObject(mLayout,"backgroundColor",new ArgbEvaluator(),0,Color.BLACK)
                    .setDuration(350)
                    .start();*/
        }
    }

    @OnClick(R.id.button_one)
    void buttonOne() {
        detach();
    }

    @OnClick(R.id.button_two)
    void buttonTwo() {
        detach();
    }

    public void detach() {
        if(mLayout!=null) {
           // mMarkerView.setState(MarkerView.STATE_HIDDEN);
            mMarkerView.animate().scaleX(1f).scaleY(1f).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
            mCardView.animate().scaleX(1f).scaleY(1f).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
            mLayout.animate().alpha(0).setDuration(350).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if(mParentLayout!=null) {
                        mUnbinder.unbind();
                        mUnbinder = null;
                        mParentLayout.removeView(mLayout);
                    }
                    mLayout = null;
                    if(mFragment!=null) mFragment.onQuickViewDetach();
                }
            });
        }
    }

    public void destroy() {
        mFragment = null;
        mParentLayout = null;
        mLayout = null;
    }
}
