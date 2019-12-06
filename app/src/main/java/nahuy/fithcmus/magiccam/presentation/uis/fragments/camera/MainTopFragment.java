package nahuy.fithcmus.magiccam.presentation.uis.fragments.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.CameraCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GLCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.presenters.ReadingFSHFilePresenter;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.MediaReceiverContract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragMainInteract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CamHandlerOwner;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraThread;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraWrapper;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by huy on 5/19/2017.
 */

public class MainTopFragment extends Fragment implements FragMainInteract, CamHandlerOwner {

    public static final int CAMERA_FRONT = 0;
    public static final int CAMERA_BACK = 1;

    private boolean isFirstTime;
    private int defaultCameraMode = CAMERA_FRONT;

    private Context context;

    @BindView(R.id.camera_surfaceView)
    GLSurfaceView mGLView;

    // A.
    private MBRender mRenderer;
    // Bridge.
    private CameraSurfaceHandler mCameraHandler;

    private CameraWrapper cameraWrapper;

    // Thread to open camera.
    CameraThread camThread;

    private int mCamWidth = 720;
    private int mCamHeight = 1080;

    public static MainTopFragment getInstance(){
        MainTopFragment mainTopFragment = new MainTopFragment();
        mainTopFragment.isFirstTime = true;
        return mainTopFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        setUpContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_top_layout, null);

        ButterKnife.bind(this, v);

        cameraWrapper = new CameraWrapper();
        mCameraHandler = new CameraSurfaceHandler(this, cameraWrapper);

        // Need to research.
        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL context.
        mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        setUpRender();
        mGLView.setRenderer(mRenderer);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        isFirstTime = false;

        mGLView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mRenderer.mouseDown(x, y);
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    mRenderer.mouseMove(x, y);
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mRenderer.clearMouse();
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        startScreen();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScreen();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void interactCamera(CameraCommander camAct) {
        invokeCommander(camAct, mCameraHandler);
    }

    @Override
    public void interactRender(GLCommander renderAct) {
        invokeCommander(renderAct, mRenderer);
    }

    @Override
    public void setListenerForST() {
        cameraWrapper.setFrameAvailableListener(this);
    }

    @Override
    public void changeRenderOrientation() {
        mRenderer.changeFace();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLView.requestRender();
    }


    private void invokeCommander(CallingAbstractCommander commander, Object receiver){
        commander.process(receiver);
    }

    private void setUpRender(){
        mRenderer = new MBRender(context, mCameraHandler, (MainActivity)getActivity());
    }

    private void setUpContext(){
        MyGLShader.setContext(context);
    }

    private void startScreen() {
        //camThread = new CameraThread(mCameraHandler);
        //camThread.execute(mCamWidth, mCamHeight, defaultCameraMode);

        mGLView.onResume();
        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setPSize(mCamWidth, mCamHeight);
            }
        });
    }

    private void stopScreen(){
        releaseCamera();
        mGLView.queueEvent(new Runnable() {
            @Override public void run() {
                // Tell the renderer that it's about to be paused so it can clean up.
                mRenderer.notifyPausing();
            }
        });
        mGLView.onPause();
    }

    /**
     * Stops camera preview, and releases the camera to the system.
     */
    private void releaseCamera() {
        mCameraHandler.sendMessage(mCameraHandler.obtainMessage(CameraSurfaceHandler.RELEASE));
    }
}
