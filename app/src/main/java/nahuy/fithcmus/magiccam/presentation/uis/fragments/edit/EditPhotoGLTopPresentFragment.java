package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bohush.geometricprogressview.GeometricProgressView;
import net.bohush.geometricprogressview.TYPE;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render.FrameImageRender;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render.ImageRender;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopGLGetBitmapCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;

/**
 * Created by nahuy on 5/24/2017.
 */

public class EditPhotoGLTopPresentFragment extends Fragment implements EditTopPresentFragmentCallback, 
        EditTopGLGetBitmapCallback {

    public static EditPhotoGLTopPresentFragment getInstance(){
        EditPhotoGLTopPresentFragment editPhotoGLTopPresentFragment = new EditPhotoGLTopPresentFragment();
        return editPhotoGLTopPresentFragment;
    }

    private Bitmap originBitmap;

    @BindView(R.id.edit_top_gl_view)
    GLSurfaceView glSurfaceView;

    private ImageRender mRenderer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_edit_top_glview, container, false);

        ButterKnife.bind(this, rootView);

        MyGLShader.setContext(getContext());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.setEGLContextClientVersion(2);     // select GLES 2.0
        mRenderer = new FrameImageRender(getContext(), this, originBitmap);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {
        callingAbstractCommander.process(mRenderer);
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public Bitmap getProduct() {
        GeometricProgressView progressView = new GeometricProgressView(getContext());
        progressView.setType(TYPE.KITE);
        progressView.setNumberOfAngles(6);
        progressView.setColor(Color.parseColor("#00897b"));
        progressView.setDuration(5000);
        ViewGroup view = (ViewGroup)getView();
        view.addView(progressView);
        mRenderer.getBitmapResult();
        synchronized (this){
            try {
                this.wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        view.removeView(progressView);
        return originBitmap;
    }

    @Override
    public void returnBitmap(Bitmap bm) {
        synchronized (this) {
            originBitmap = bm;
            this.notifyAll();
        }
    }

}
