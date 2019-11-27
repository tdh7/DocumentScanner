package com.tdh7.documentscanner.controller.picker;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.filter.FilterImageView;
import com.tdh7.documentscanner.controller.filter.canvas.CanvasFilter;
import com.tdh7.documentscanner.controller.filter.canvas.QuickViewCanvasFilter;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.ui.widget.MarkerView;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Tool;
import com.tdh7.documentscanner.util.Util;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CropEdgeQuickView {
    private static final String TAG = "CropEdgeQuickView";
    private ViewGroup mParentLayout;
    private View mLayout;

    private static final int MODE_INSIDE_MAIN_SCREEN = 0;
    private static final int MODE_INSIDE_CAMERA_PICKER = 1;

    public RawBitmapDocument getBitmapDocument() {
        return mBitmapDocument;
    }

    public void setBitmapDocument(RawBitmapDocument bitmapDocument) {
        mBitmapDocument = bitmapDocument;
    }

    public interface QuickViewCallback {
        public ViewGroup getParentLayout();
        public void onQuickViewAttach();
        public void onQuickViewDetach(float[] points);
    }

    public interface QuickViewActionCallback extends QuickViewCallback {
        public void onActionClick();
    }

    private QuickViewCallback mQuickViewCallback;

    public boolean isAttached() {
        return mLayout!=null;
    }

    @BindView(R.id.filter_image_view)
    FilterImageView mFilterImageView;

    @BindView(R.id.card_view)
    FrameLayout mCardView;

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

    Unbinder mUnbinder;

    public CropEdgeQuickView() {
        Util.getDefaultValue(points);
    }

    @BindView(R.id.status_bar)
    View mStatusView;

    @BindView(R.id.markerView)
    MarkerView mMarkerView;

    @BindView(R.id.constraint_parent)
    ConstraintLayout mConstraintParent;

    private float mStatusHeight = 0;
    private float[] points = new float[8];

    private void setPoints(float[] p) {
        points = p;
        if(mMarkerView!=null)
        mMarkerView.setPoints(p);
    }

    public void getCurrentEdge(float[] point) {
        mMarkerView.getPoints(point);
    }
    private RawBitmapDocument mBitmapDocument;
    public void attachAndPresent(QuickViewCallback callback, RawBitmapDocument document) {
        mParentLayout = callback.getParentLayout();
        mQuickViewCallback = callback;
        if(mParentLayout==null)
            return;

        int rotationDegrees = document.mRotateDegree;
        Bitmap bitmap = document.mOriginalBitmap;
        mBitmapDocument = document;

        if(mLayout==null) {
            mLayout = LayoutInflater.from(mParentLayout.getContext()).inflate(R.layout.crop_edge_quick_view, mParentLayout, false);
            mUnbinder = ButterKnife.bind(this,mLayout);
            mStatusHeight = Tool.getStatusHeight(mParentLayout.getResources());
            ((ViewGroup.MarginLayoutParams)mStatusView.getLayoutParams()).height = (int) mStatusHeight;


            View cameraCardView =  mParentLayout.findViewById(R.id.camera_card_view);
            if(cameraCardView!=null)
                ((ViewGroup.MarginLayoutParams)mCardView.getLayoutParams()).topMargin = ((ViewGroup.MarginLayoutParams)cameraCardView.getLayoutParams()).topMargin;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mConstraintParent);
            constraintSet.setDimensionRatio(R.id.card_view,document.mViewPort[0]+":"+document.mViewPort[1]);
            constraintSet.applyTo(mConstraintParent);

            mParentLayout.addView(mLayout);
            CanvasFilter canvasFilter = mFilterImageView.getFilter();
            Log.d(TAG, "attachAndPresent: image was rotated by "+rotationDegrees);
            if(canvasFilter instanceof QuickViewCanvasFilter)
                ((QuickViewCanvasFilter) canvasFilter).setRotateValue(rotationDegrees);
            mFilterImageView.setBitmap(bitmap);

            // 0.78
            mCardView.animate().scaleX(0.78f).scaleY(0.78f).setDuration(550).setInterpolator(new OvershootInterpolator()).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int w = 0,h = 0;
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(w==0)
                    {
                    w = mMarkerView.getViewWidth();
                    h = mMarkerView.getViewHeight();
                    }
                    float value = (float) animation.getAnimatedValue();
                    float paddingPercent = (1 - 0.78f)*value/2;
                    mMarkerView.setPaddingAndInvalidate((int)(w*paddingPercent),(int)(h*paddingPercent),(int)(w*paddingPercent), (int)(h*paddingPercent));
                }
            }).start();
            //mMarkerView.animate().scaleX(0.78f).scaleY(0.78f).setDuration(550).setInterpolator(new OvershootInterpolator()).start();
            ScanUtils.boundToViewPort(document.mEdgePoints, new float[]{1,1});
            mMarkerView.setPoints(document.mEdgePoints);
            mQuickViewCallback.onQuickViewAttach();
          // mLayout.setAlpha(0);
          //  mLayout.animate().alpha(1f).setDuration(250).start();
           /* ObjectAnimator.ofObject(mLayout,"backgroundColor",new ArgbEvaluator(),0,Color.BLACK)
                    .setDuration(350)
                    .start();*/
        }
    }

    @OnClick(R.id.back_button)
    void back() {
        detach();
    }

    @OnClick(R.id.action_button)
    void save() {
        if(mQuickViewCallback instanceof QuickViewActionCallback) {
            mMarkerView.getPoints(mBitmapDocument.mEdgePoints);
            ((QuickViewActionCallback) mQuickViewCallback).onActionClick();
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

    @BindView(R.id.action_button)
    View mActionButton;

    public void showActionButton() {
        mActionButton.setVisibility(View.VISIBLE);
    }

    public void detach() {
        mMarkerView.getPoints(points);
        mMarkerView.getPoints(mBitmapDocument.mEdgePoints);
        if(mLayout!=null) {
           // mMarkerView.setState(MarkerView.STATE_HIDDEN);
          //  mMarkerView.animate().scaleX(1f).scaleY(1f).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
            mCardView.animate().scaleX(1f).scaleY(1f).setDuration(350).setInterpolator(new OvershootInterpolator()).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int w = 0,h = 0;
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(w==0)
                    {
                        w = mMarkerView.getViewWidth();
                        h = mMarkerView.getViewHeight();
                    }
                    float value = 1 - (float)  animation.getAnimatedValue();
                    float paddingPercent = (1 - 0.78f)*value/2;
                    mMarkerView.setPaddingAndInvalidate((int)(w*paddingPercent),(int)(h*paddingPercent),(int)(w*paddingPercent), (int)(h*paddingPercent));
                }
            }).start();
            mLayout.animate().alpha(0).setDuration(350).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if(mParentLayout!=null) {
                        mUnbinder.unbind();
                        mUnbinder = null;
                        mParentLayout.removeView(mLayout);
                    }
                    mLayout = null;
                    if(mQuickViewCallback!=null) mQuickViewCallback.onQuickViewDetach(points);
                    mQuickViewCallback = null;
                }
            });
        }
    }
}
