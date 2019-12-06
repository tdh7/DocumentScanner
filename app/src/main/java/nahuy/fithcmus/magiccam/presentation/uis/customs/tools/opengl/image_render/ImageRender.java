package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.SurfaceInteractHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.FullFrameRect;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopGLGetBitmapCallback;

/**
 * Created by huy on 6/21/2017.
 */

public class ImageRender implements GLSurfaceView.Renderer {

    private static final String TAG = ImageRender.class.getName();
    private static final boolean VERBOSE = false;
    protected static final int UNUSED_WHAT = -1;

    private FullFrameRect mFullScreen;

    private final float[] mSTMatrix = new float[16];
    protected int mTextureId;

    private int mFrameCount;

    // width/height of the incoming camera preview frames
    private boolean mIncomingSizeUpdated;
    protected int mIncomingWidth;
    protected int mIncomingHeight;
    protected int containerWidth;
    protected int containerHeight;

    protected MyGLEffectShader mCurrentFilter;
    protected MyGLEffectShader mNewFilter;

    private EditTopGLGetBitmapCallback bmCalback;
    private Context context;
    private Bitmap bm;
    private boolean getBitmap = false;
    private int resId;

    public ImageRender(Context context, EditTopGLGetBitmapCallback bmCallback, Bitmap bm) {
        this.context = context;
        this.bmCalback = bmCallback;
        this.bm = bm;

        mTextureId = -1;
        mFrameCount = -1;

        mIncomingSizeUpdated = false;
        mIncomingWidth = mIncomingHeight = -1;

        // We could preserve the old filter mode, but currently not bothering.
        mCurrentFilter = new MyGLEffectShader("","","", null);
        mNewFilter = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
    }

    /**
     * Changes the filter that we're applying to the camera preview.
     */
    public void changeFilterMode(MyGLEffectShader filter) {
        mNewFilter = filter;
    }

    /**
     * Updates the filter program.
     */
    protected void updateFilter() {
        // Do we need a whole new program?  (We want to avoid doing this if we don't have
        // too -- compiling a program could be expensive.)
        mFullScreen.changeFilter(mNewFilter);
        // If we created a new program, we need to initialize the texture width/height.
        mIncomingSizeUpdated = true;

        // mVideoEncoder.changeFS(programType);

        // Update the filter kernel (if any).
        mCurrentFilter = mNewFilter;
    }

    protected void updatePSize() {
        mFullScreen.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
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
        if (mFullScreen != null) {
            mFullScreen.release(false);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             //  to be destroyed
        }
        mIncomingWidth = mIncomingHeight = -1;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        mFullScreen = new FullFrameRect(context, 1);

        int result = mFullScreen.createTextureObject(context, bm);
        mIncomingWidth = bm.getWidth();
        mIncomingHeight = bm.getHeight();

        mTextureId = result;
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

        // TODO Auto-generated method stub
        if (VERBOSE) Log.d(TAG, "onDrawFrame tex=" + mTextureId);
        boolean showBox = false;

        if(getBitmap == true){
            bmCalback.returnBitmap(createBitmapFromGLSurface(0, 0, containerWidth, containerHeight, gl));
            getBitmap = false;
        }
        // Latch the latest frame.  If there isn't anything new, we'll just re-use whatever
        // was there before.
        // mSurfaceTexture.updateTexImage();

        if (mIncomingWidth <= 0 || mIncomingHeight <= 0) {
            // Texture size isn't set yet.  This is only used for the filters, but to be
            // safe we can just skip drawing while we wait for the various races to resolve.
            // (This seems to happen if you toggle the screen off/on with power button.)
            Log.i(TAG, "Drawing before incoming texture size set; skipping");
            return;
        }

        // Update the filter, if necessary.
        if (mCurrentFilter.compareTo(mNewFilter) != 1) {
            updateFilter();
        }

        if (mIncomingSizeUpdated) {
            updatePSize();
            mIncomingSizeUpdated = false;
        }

        // Draw the video frame.
        Matrix.setIdentityM(mSTMatrix, 0);
        // mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix);
    }

    public void getBitmapResult(){
        getBitmap = true;
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

