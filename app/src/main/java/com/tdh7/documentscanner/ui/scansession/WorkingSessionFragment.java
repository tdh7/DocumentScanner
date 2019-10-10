package com.tdh7.documentscanner.ui.scansession;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.util.Util;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.result.BitmapPhoto;

public class WorkingSessionFragment extends NavigationFragment {
    private static final String TAG = "WorkingSessionFragment";

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

    public static WorkingSessionFragment newInstance(BitmapPhoto bitmap) {

        WorkingSessionFragment fragment = new WorkingSessionFragment();
        fragment.mBitmapPhoto = bitmap;
        return fragment;
    }

    private BitmapPhoto mBitmapPhoto;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.working_session,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        if(mBitmapPhoto !=null) {
            if(mBitmapPhoto.rotationDegrees==0)
            mImageView.setImageBitmap(mBitmapPhoto.bitmap);
            else AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap result = Util.rotateBitmap(mBitmapPhoto.bitmap,-mBitmapPhoto.rotationDegrees);
                    mImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(result);
                        }
                    });
                }
            });
        }
    }

    @BindView(R.id.status_bar)
    View mStatusView;

    @BindView(R.id.image_view)
    ImageView mImageView;

    @Override
    public void onSetStatusBarMargin(int value) {

    }

    @Override
    public int defaultTransition() {
        return PresentStyle.FADE;
    }


}
