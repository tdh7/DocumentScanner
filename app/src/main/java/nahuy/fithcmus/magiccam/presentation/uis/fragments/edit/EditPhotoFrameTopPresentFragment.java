package nahuy.fithcmus.magiccam.presentation.uis.fragments.edit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bohush.geometricprogressview.GeometricProgressView;
import net.bohush.geometricprogressview.TYPE;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render.FrameImageRender;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopGLGetBitmapCallback;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopPresentFragmentCallback;

/**
 * Created by huy on 6/1/2017.
 */

public class EditPhotoFrameTopPresentFragment extends Fragment implements EditTopPresentFragmentCallback, EditTopGLGetBitmapCallback {

    private Bitmap originBitmap;
    private FrameImageRender mRenderer;

    @BindView(R.id.edit_top_gl_view)
    GLSurfaceView frameImageView;

    public static EditPhotoFrameTopPresentFragment getInstance(){
        EditPhotoFrameTopPresentFragment editPhotoFrameTopPresentFragment = new EditPhotoFrameTopPresentFragment();
        return editPhotoFrameTopPresentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_top_glview, null);

        ButterKnife.bind(this, v);

        MyGLShader.setContext(getContext());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        frameImageView.setEGLContextClientVersion(2);     // select GLES 2.0
        mRenderer = new FrameImageRender(getContext(), this, originBitmap);
        frameImageView.setRenderer(mRenderer);
        frameImageView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        frameImageView.queueEvent(new Runnable() {
            @Override public void run() {
                // Tell the renderer that it's about to be paused so it can clean up.
                mRenderer.notifyPausing();
            }
        });
        frameImageView.onPause();
    }

    @Override
    public Fragment getFragment(Bitmap bm) {
        this.originBitmap = bm;
        return this;
    }

    @Override
    public void process(CallingAbstractCommander callingAbstractCommander) {
        callingAbstractCommander.process(mRenderer);
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
