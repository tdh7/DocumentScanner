package nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit;

import android.content.Context;
import android.opengl.GLES20;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.nio.FloatBuffer;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.TextureProgram;

/**
 * Created by huy on 6/18/2017.
 */

public class MyToolGLShader extends MyGLShader {

    private boolean usingQuantity = false;
    private boolean usingColor = false;
    private int resId = R.mipmap.ic_launcher;
    private float[] color = {255.0f/255.0f,255.0f/255.0f,255.0f/255.0f};
    private float quantity;
    private boolean clear = false;
    protected boolean usingClear = false;

    public MyToolGLShader(String shaderName, String fShader,  String imgPath, String... renderBuffer) {
        super(shaderName, fShader, imgPath, renderBuffer);
    }

    public MyToolGLShader(String shaderName, String fShader, String bufferOrder, String imgPath, String... renderBuffer) {
        super(shaderName, fShader, bufferOrder, imgPath, renderBuffer);
    }

    public void setUsingQuantity(boolean usingQuantity) {
        this.usingQuantity = usingQuantity;
    }

    public void setUsingColor(boolean usingColor) {
        this.usingColor = usingColor;
    }

    public boolean isUsingQuantity() {
        return usingQuantity;
    }

    public boolean isUsingColor() {
        return usingColor;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void clearSurface(){
        this.clear = true;
    }

    @Override
    public void setAssetImageViewHeader(Context context, ImageView filter_main_img) {
        Glide.with(context).load(resId).into(filter_main_img);
    }

    public boolean isUsingClear() {
        return usingClear;
    }

    public void setUsingClear(boolean usingClear) {
        this.usingClear = usingClear;
    }

    @Override
    protected void setDefault(TextureProgram textureProgram, int program,
                              float[] mvpMatrix, FloatBuffer vertexBuffer,
                              int coordsPerVertex, int vertexStride,
                              float[] texMatrix, FloatBuffer texBuffer,
                              int texStride, int[] channelTexId) {
        super.setDefault(textureProgram, program, mvpMatrix,
                vertexBuffer, coordsPerVertex,
                vertexStride, texMatrix, texBuffer,
                texStride, channelTexId);

        int iClear = GLES20.glGetUniformLocation(program, "iClear");
        if(iClear != -1) {
            GLES20.glUniform1f(iClear, clear ? 1.0f : 0.0f);
            clear = false;
        }

        int iSize = GLES20.glGetUniformLocation(program, "iSize");
        if(iSize != -1) {
            GLES20.glUniform1f(iSize, quantity);
        }

        int iColor = GLES20.glGetUniformLocation(program, "brushCol");
        GLES20.glUniform3fv(iColor, 1, FloatBuffer.wrap(new float[]{color[0], color[1], color[2]}));
    }
}
