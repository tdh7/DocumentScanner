package nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.TextureProgram;

/**
 * Created by huy on 6/16/2017.
 */

public class MyDrawGLShader extends MyToolGLShader {

    public MyDrawGLShader(){
        super("Huy", "free_draw.fsh", "B1|B1,M1", "",
                new String[]{"free_draw_buf_a.fsh"});
    }

}
