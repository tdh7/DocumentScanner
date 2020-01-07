package com.tdh7.documentscanner.ui.scansession;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.controller.filter.FilterImageView;
import com.tdh7.documentscanner.controller.filter.canvas.CanvasFilter;
import com.tdh7.documentscanner.controller.filter.canvas.QuickViewCanvasFilter;
import com.tdh7.documentscanner.ui.fragments.BaseFragment;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.result.BitmapPhoto;

public class WorkingSessionFragment extends BaseFragment {
    private static final String TAG = "WorkingSessionFragment";

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

    public static WorkingSessionFragment newInstance() {
        WorkingSessionFragment fragment = new WorkingSessionFragment();
        return fragment;
    }

    public static WorkingSessionFragment newInstance(Bitmap bitmap) {

        WorkingSessionFragment fragment = new WorkingSessionFragment();
        fragment.mBitmapObject = bitmap;
        return fragment;
    }

    public static WorkingSessionFragment newInstance(BitmapPhoto bitmap) {

        WorkingSessionFragment fragment = new WorkingSessionFragment();
        fragment.mBitmapObject = bitmap;
        return fragment;
    }

    private Object mBitmapObject;

    @Override
    public int contentLayout() {
        return R.layout.crop_edge_quick_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        if(mBitmapObject instanceof Bitmap) {
           mImageView.setBitmap((Bitmap) mBitmapObject);
        } else if(mBitmapObject instanceof BitmapPhoto) {
            CanvasFilter filter = mImageView.getFilter();
            if(filter instanceof QuickViewCanvasFilter) ((QuickViewCanvasFilter) filter).setRotateValue( 360 - ((BitmapPhoto) mBitmapObject).rotationDegrees);
            mImageView.setBitmap(((BitmapPhoto) mBitmapObject).bitmap);
        } else {
            mImageView.setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ys));
        }
    }

    @BindView(R.id.status_bar)
    View mStatusView;

    @BindView(R.id.filter_image_view)
    FilterImageView mImageView;

    @Override
    public void onSetStatusBarMargin(int value) {

    }

    @Override
    public int defaultTransition() {
        return PresentStyle.NONE;
    }


}
