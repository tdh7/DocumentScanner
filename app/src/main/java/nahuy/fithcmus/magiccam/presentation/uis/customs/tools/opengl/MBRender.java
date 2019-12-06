package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.IntBuffer;
import java.sql.Timestamp;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.MediaAccessWay;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.MediaReceiverContract;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.PhotoAccessWay;
import nahuy.fithcmus.magiccam.presentation.uis.customs.sub_processes.VideoAccessWay;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.SurfaceInteractHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.gl_action.MBRenderAction;

import static android.R.attr.x;
import static android.R.attr.y;
import static com.googlecode.mp4parser.authoring.tracks.h264.SliceHeader.SliceType.P;

public class MBRender implements GLSurfaceView.Renderer {

    // Constants
    private static final String TAG = MBRender.class.getName();
	private static final boolean VERBOSE = false;
    protected static final int UNUSED_WHAT = -1;

    // State
    protected boolean mIncomingSizeUpdated;

    protected MyGLEffectShader mCurrentFilter;
    protected MyGLEffectShader mNewFilter;

    protected MyToolGLShader mCurrentTool;
    protected MyToolGLShader mNewTool;

    // Local
    protected Context context;
    protected FullFrameRect mFullScreen;

    protected final float[] mSTMatrix = new float[16];
    protected int mTextureId;

    // width/height of the incoming camera preview frames
    protected int mIncomingWidth;
    protected int mIncomingHeight;
    protected int containerWidth;
    protected int containerHeight;

    protected SurfaceTexture mSurfaceTexture;

    // Reference

    protected CameraSurfaceHandler mCameraHandler;
    protected SurfaceInteractHandler sIHandler;
    protected MediaReceiverContract mediaReceiverContract;

    protected MBRenderAction extendAction = new MBCaptureRender();
    protected MediaAccessWay maw;

    protected int currentMode = 0;

    protected MBRender(){}

    public MBRender(Context context) {
        this.context = context;
        mTextureId = -1;

        mIncomingSizeUpdated = false;
        mIncomingWidth = mIncomingHeight = -1;

        mCurrentFilter = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
        mNewFilter = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
    }

    public MBRender(Context context, CameraSurfaceHandler mCameraHandler, MediaReceiverContract mediaReceiverContract) {
		this.context = context;
        this.mCameraHandler = mCameraHandler;
        this.mediaReceiverContract = mediaReceiverContract;
		
		mTextureId = -1;

        mIncomingSizeUpdated = false;
        mIncomingWidth = mIncomingHeight = -1;

        // We could preserve the old filter mode, but currently not bothering.
        mCurrentFilter = new MyGLEffectShader("", "", "", new String[]{});
        mNewFilter = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
        mCurrentTool = null;
        mNewTool = null;
	}

    /**
     * Set up type of action for render
     */
    public void switchModeRecord(){
        extendAction = new MBRecordRender();
        maw = new VideoAccessWay(context, mediaReceiverContract);
    }

    public void switchModeCapture(){
        extendAction = new MBCaptureRender();
        maw = new PhotoAccessWay(context, mediaReceiverContract);
    }

    /**
     * Changes the filter that we're applying to the camera preview.
     */
    public void changeFilterMode(MyGLEffectShader filter) {
        mNewFilter = filter;
    }

    public void changeTool(MyToolGLShader mNewTool) {
        this.mNewTool = mNewTool;
    }
    
    /**
     * Updates the filter program.
     */
    protected void updateFilter() {
        mFullScreen.changeFilter(mNewFilter);
        // If we created a new program, we need to initialize the texture width/height.
        mIncomingSizeUpdated = true;

        extendAction.updateFilter();
        // mVideoEncoder.changeFS(programType);
        
        // Update the filter kernel (if any).
        mCurrentFilter = mNewFilter;
    }

    public MyToolGLShader getCurrentTool(){
        return mCurrentTool;
    }

    public MyGLEffectShader getmCurrentFilter() {
        return mCurrentFilter;
    }

    protected void updateTool() {
        mFullScreen.changeTool(mNewTool);
        // If we created a new program, we need to initialize the texture width/height.
        mIncomingSizeUpdated = true;

        // In case change mode while in active
        extendAction.updateTool();
        // mVideoEncoder.changeFS(programType);

        // Update the filter kernel (if any).
        mCurrentTool = mNewTool;
    }

    protected void updatePSize() {
		mFullScreen.getProgram().setTexSize(containerWidth, containerHeight);
        // In case change mode while in active
        extendAction.updatePSize();
	}

	public void setPSize(int w, int h){
		Log.d(TAG, "setCameraPreviewSize");
        mIncomingWidth = w;
        mIncomingHeight = h;
        mIncomingSizeUpdated = true;
	}
	
	/**
     * Notifies the renderer thread that the activity is pausing.
     * <p>
     * For best results, call this *after* disabling Camera preview.
     */
    public void notifyPausing() {
        if (mSurfaceTexture != null) {
        	mCameraHandler.sendMessage(mCameraHandler.obtainMessage(CameraSurfaceHandler.RELEASE_ST));
        }
        if (mFullScreen != null) {
            mFullScreen.release(true);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             //  to be destroyed
        }
        mNewFilter = mCurrentFilter;
        mCurrentFilter = new MyGLEffectShader("", "", "", null);
        mNewTool = mCurrentTool;
        mCurrentTool = null;
        mIncomingWidth = mIncomingHeight = -1;
    }
	
	public void action(){
        extendAction.action();
    }

    protected void performAction(GL10 gl){
        extendAction.performAction(gl);
    }
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(TAG, "onSurfaceCreated");

        // Set up the texture blitter that will be used for on-screen display.  This
        // is *not* applied to the recording, because that uses a separate shader.
        mFullScreen = new FullFrameRect(context);


        mTextureId = mFullScreen.createTextureObject();

        // Create a SurfaceTexture, with an external texture, in this EGL context.  We don't
        // have a Looper in this thread -- GLSurfaceView doesn't create one -- so the frame
        // available messages will arrive on the main thread.
        mSurfaceTexture = new SurfaceTexture(mTextureId);

        // Tell the UI thread to enable the camera preview.
        mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                CameraSurfaceHandler.SURFACE_COMING, mSurfaceTexture));
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onSurfaceChanged " + width + "x" + height);
        containerWidth = width;
        containerHeight = height;
	}

	// Template method.
	@Override
	public void onDrawFrame(GL10 gl) {

        // TODO Aucto-generated method stub
        if (VERBOSE) Log.d(TAG, "onDrawFrame tex=" + mTextureId);

        // Latch the latest frame.  If there isn't anything new, we'll just re-use whatever
        // was there before.
        mSurfaceTexture.updateTexImage();

        // Vary action here.
        performAction(gl);

        if (mIncomingWidth <= 0 || mIncomingHeight <= 0) {
            // Texture size isn't set yet.  This is only used for the filters, but to be
            // safe we can just skip drawing while we wait for the various races to resolve.
            // (This seems to happen if you toggle the screen off/on with power button.)
            Log.i(TAG, "Drawing before incoming texture size set; skipping");
            return;
        }

        // Update the filter, if necessary.
        if(mNewFilter != null) {
            if(mCurrentFilter == null){
                updateFilter();
            }
            else {
                if (mCurrentFilter.compareTo(mNewFilter) != 1) {
                    updateFilter();
                }
            }
        }

        if(mNewTool != null) {
            if(mCurrentTool == null){
                updateTool();
            }
            else {
                if (mCurrentTool.compareTo(mNewTool) != 1) {
                    updateTool();
                }
            }
        }

        if (mIncomingSizeUpdated) {
            updatePSize();
            mIncomingSizeUpdated = false;
        }

        // Draw the video frame.
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix);

    }

    public void changeFace() {
        if (mFullScreen != null) {
            mFullScreen.changeFace();
            if(extendAction instanceof MBRecordRender){
                MBRecordRender recordRender = (MBRecordRender)extendAction;
                recordRender.changeFace();
            }
        }
    }

    public void mouseMove(float x, float y){
        if(mFullScreen != null){
            mFullScreen.mouseMove(x, y);
            if(extendAction instanceof MBRecordRender){
                MBRecordRender recordRender = (MBRecordRender)extendAction;
                recordRender.mouseMouse(x, y);
            }
        }
    }

    public void mouseDown(float x, float y) {
        if(mFullScreen != null){
            mFullScreen.mouseDown(x, y);
            if(extendAction instanceof MBRecordRender){
                MBRecordRender recordRender = (MBRecordRender)extendAction;
                recordRender.mouseDown(x, y);
            }
        }
    }

    public void changeToolColor(float[] color) {
        if(mCurrentTool != null) {
            mCurrentTool.setColor(color);
        }
    }

    public void changeToolSize(float size) {
        if (mCurrentTool != null) {
            mCurrentTool.setQuantity(size);
        }
    }

    public void clearToolSurface() {
        if (mCurrentTool != null) {
            mCurrentTool.clearSurface();
        }
    }

    public void clearMouse() {
        if(mFullScreen != null){
            mFullScreen.mouseUp();
            if(extendAction instanceof MBRecordRender){
                MBRecordRender recordRender = (MBRecordRender)extendAction;
                recordRender.mouseUp();
            }
        }
    }

    private class MBRecordRender implements MBRenderAction {

        private Boolean record;
        private boolean isFirstTimeUpdate = true;

        private TextureMovieEncoder mVideoEncoder = new TextureMovieEncoder(context);
        private File mOutputFile;
        private long recordTime;

        public MBRecordRender() {
            long time = System.currentTimeMillis();
            mOutputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                    , "Huy_" + time + ".mp4");
        }

        @Override
        public void action() {
            // TODO Auto-generated method stub
            if(record == null){
                record = true;
                recordTime = System.currentTimeMillis();
            }
            else{
                record = !record;
            }
        }

        @Override
        public void performAction(GL10 gl) {
            if (record == null) {
                return;
            }

            if (record == true) {
                mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        mOutputFile, containerWidth, containerHeight, 6000000, EGL14.eglGetCurrentContext()), maw);

                if(isFirstTimeUpdate) {
                    updatePSize();
                    mVideoEncoder.changeFS(mCurrentFilter);
                    mVideoEncoder.changeTool(mCurrentTool);
                    isFirstTimeUpdate = false;
                }

                // Set the video encoder's texture name.  We only need to do this once, but in the
                // current implementation it has to happen after the video encoder is started, so
                // we just do it here.
                //
                // TODO: be less lame.
                mVideoEncoder.setTextureId(mTextureId);

                // Tell the video encoder thread that a new frame is available.
                // This will be ignored if we're not actually recording.
                mVideoEncoder.frameAvailable(mSurfaceTexture);
            }
            if (record == false || (System.currentTimeMillis() - recordTime) / 1000 > 8) {
                mVideoEncoder.stopRecording();
                isFirstTimeUpdate = true;
                record = null;
            }
        }

        public void updateFilter() {
            // TODO Auto-generated method stub
            mVideoEncoder.changeFS(mNewFilter);
        }

        @Override
        public void updateTool() {
            mVideoEncoder.changeTool(mNewTool);
        }

        public void updatePSize() {
            // TODO Auto-generated method stub
            mVideoEncoder.changePSize(containerWidth, containerHeight);
        }

        public void mouseUp() {
            mVideoEncoder.mouseUp();
        }

        public void mouseMouse(float x, float y) {
            mVideoEncoder.mouseMouse(x, y);
        }

        public void mouseDown(float x, float y) {
            mVideoEncoder.mouseDown(x, y);
        }

        public void changeFace() {
            mVideoEncoder.changeFace();
        }
    }

    private class MBCaptureRender implements MBRenderAction {

        private boolean capture = false;

        public MBCaptureRender() {
            super();
        }

        @Override
        public void action() {
            // TODO Auto-generated method stub
            capture = true;
        }

        @Override
        public void performAction(GL10 gl) {
            if(capture){
                Bitmap bm = this.createBitmapFromGLSurface(0, 0, containerWidth, containerHeight, gl);
                maw.processMedia(bm);
                capture = false;
            }
        }

        @Override
        public void updateFilter() {

        }

        @Override
        public void updateTool() {

        }

        @Override
        public void updatePSize() {

        }

        private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
                throws OutOfMemoryError {
            int bitmapBuffer[] = new int[w * h];
            int bitmapSource[] = new int[w * h];
            IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
            intBuffer.position(0);

            try {
                gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
                int offset1, offset2;
                for (int i = 0; i < h; i++) {
                    offset1 = i * w;
                    offset2 = (h - i - 1) * w;
                    for (int j = 0; j < w; j++) {
                        int texturePixel = bitmapBuffer[offset1 + j];
                        int blue = (texturePixel >> 16) & 0xff;
                        int red = (texturePixel << 16) & 0x00ff0000;
                        int pixel = (texturePixel & 0xff00ff00) | red | blue;
                        bitmapSource[offset2 + j] = pixel;
                    }
                }
            } catch (GLException e) {
                return null;
            }

            return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        }
    }
}