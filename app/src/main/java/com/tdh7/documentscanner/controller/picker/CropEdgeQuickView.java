package com.tdh7.documentscanner.controller.picker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.cardview.widget.CardView;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.filter.FilterImageView;
import com.tdh7.documentscanner.controller.filter.canvas.CanvasFilter;
import com.tdh7.documentscanner.controller.filter.canvas.QuickViewCanvasFilter;
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

    @BindView(R.id.filter_image_view)
    FilterImageView mFilterImageView;

    @BindView(R.id.card_view)
    CardView mCardView;

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

    private Unbinder mUnbinder;
    public void attachThenPresent(Bitmap bitmap, int rotationDegrees) {
        if(mFragment==null||mParentLayout==null)
            return;
        if(mLayout==null) {
            mLayout = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.crop_edge_quick_view, mParentLayout, false);
            mUnbinder = ButterKnife.bind(this,mLayout);
            mStatusHeight = mFragment.mStatusHeight;
            ((ViewGroup.MarginLayoutParams)mStatusView.getLayoutParams()).height = (int) mStatusHeight;
            ((ViewGroup.MarginLayoutParams)mCardView.getLayoutParams()).topMargin =    ((ViewGroup.MarginLayoutParams)mParentLayout.findViewById(R.id.camera_card_view).getLayoutParams()).topMargin;;
            mParentLayout.addView(mLayout);
            CanvasFilter canvasFilter = mFilterImageView.getFilter();
            Log.d(TAG, "attachThenPresent: image was rotated by "+rotationDegrees);
            if(canvasFilter instanceof QuickViewCanvasFilter)
                ((QuickViewCanvasFilter) canvasFilter).setRotateValue(rotationDegrees);
            mFilterImageView.setBitmap(bitmap);
            mLayout.setBackgroundColor(Color.BLACK);
            mCardView.animate().scaleX(0.78f).scaleY(0.78f).setDuration(550).setInterpolator(new OvershootInterpolator()).start();

            AutoCapturer capturer = mFragment.getEdgeFrameProcessor().getAutoCapturer();
            if(capturer!=null) {
                float[] points;// = new float[8];
               points = mFragment.mAverages;
                mMarkerView.setPoints(points);
                Log.d(TAG, "setPoints: " +
                        "p[0] = ("+points[0]+"; "+points[4]+"), " +
                        "p[1] = ("+points[1]+"; "+points[5]+"), "+
                        "p[2] = ("+points[2]+"; "+points[6]+"), "+
                        "p[3] = ("+points[3]+"; "+points[7]+")");
            }

            mFragment.onQuickViewAttach();
          // mLayout.setAlpha(0);
         //   mLayout.animate().alpha(0.85f).setDuration(350).start();
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
