package com.tdh7.documentscanner.ui.scansession;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.result.BitmapPhoto;

public class WorkingSessionFragment extends NavigationFragment {
    private static final String TAG = "WorkingSessionFragment";

    @BindDimen(R.dimen.dp_unit)
    float mDpUnit = 1;

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

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.working_session,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        if(mBitmapObject instanceof Bitmap) {
           mImageView.setImageBitmap((Bitmap) mBitmapObject);
        } else if(mBitmapObject instanceof BitmapPhoto) {
          //  mImageView.setRotation(-((BitmapPhoto) mBitmapPhoto).rotationDegrees);
            mImageView.setImageBitmap(((BitmapPhoto) mBitmapObject).bitmap);
            rotateImage(mImageView,((BitmapPhoto) mBitmapObject).bitmap,- ((BitmapPhoto) mBitmapObject).rotationDegrees);
        }
    }

    private void rotateImage(ImageView imageView,Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        if(imageView.getDrawable()!=null)
        matrix.postRotate(angle, bitmap.getHeight()/2,bitmap.getWidth()/2);
        imageView.setImageMatrix(matrix);
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
        return PresentStyle.NONE;
    }


}
