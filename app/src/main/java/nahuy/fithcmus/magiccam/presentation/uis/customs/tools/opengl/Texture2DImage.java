package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/21/2017.
 */

public class Texture2DImage extends Texture2dProgram {

    public Texture2DImage(Context context) {
        this.context = context;
        effectShader = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
        defaultDrawable = new Drawable2d(Drawable2d.Prefab.IMAGE_FULL_RECTANGLE);
        // effectShader.addFilterChannel(new FilterChannel("lut1.png", 0, 0, false));
    }

    @Override
    public void draw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount,
                     int coordsPerVertex, int vertexStride, float[] texMatrix,
                     FloatBuffer texBuffer, int textureId, int texStride) {
        iFrame++;

        //GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        effectShader.draw(this, mvpMatrix, vertexBuffer, firstVertex, vertexCount,
                coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

    }
}
