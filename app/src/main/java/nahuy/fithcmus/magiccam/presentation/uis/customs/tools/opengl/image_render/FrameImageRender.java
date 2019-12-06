package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLException;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.IntBuffer;

import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.SurfaceInteractHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.FullFrameRect;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditTopGLGetBitmapCallback;

public class FrameImageRender extends ImageRender {

	public FrameImageRender(Context context, EditTopGLGetBitmapCallback bmCallback, Bitmap bm) {
		super(context, bmCallback, bm);
        // We could preserve the old filter mode, but currently not bothering.
        mNewFilter = new MyGLEffectShader("", "frame_function.fsh", "M1,F1", "", new String[]{});
        mNewFilter.addFilterChannelOnly(new FilterChannel("frame/frame0.png", 0, 0, true));
	}

    public void changeFrame(Frame frame){
        mCurrentFilter.addFilterChannelOnly(new FilterChannel(frame.getPath(),0,0,false));
    }

}
