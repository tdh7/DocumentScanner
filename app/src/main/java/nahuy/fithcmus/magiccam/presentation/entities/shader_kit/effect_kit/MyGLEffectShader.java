package nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit;

import android.opengl.GLES20;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.RenderBuffer;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.TextureProgram;

import static android.view.View.Y;

/**
 * Created by huy on 6/18/2017.
 */

public class MyGLEffectShader extends MyGLShader {

    private Integer id;
    private String downloadLink;

    // Determine if user using toolbox
    private boolean isUsingToolKit = false;

    private RenderBuffer effectOutput;

    public MyGLEffectShader(String shaderName, String fShader, String imgPath, String... renderBuffer) {
        super(shaderName, fShader, imgPath, renderBuffer);
    }

    public MyGLEffectShader(String shaderName, String fShader, String bufferOrder, String imgPath, String... renderBuffer) {
        super(shaderName, fShader, bufferOrder, imgPath, renderBuffer);
    }

    @Override
    protected void setUpBuffer(TextureProgram textureProgram) {
        super.setUpBuffer(textureProgram);
        if(bufferList == null) {
            // No other buffer using for this effect
            bufferList = new ArrayList<>();
            effectOutput = new RenderBuffer(textureProgram.getCanvasWidth(), textureProgram.getCanvasHeight(),
                    GLES20.GL_TEXTURE3);
            bufferList.add(effectOutput);
        }
        if(bufferList != null && effectOutput == null){
            effectOutput = new RenderBuffer(textureProgram.getCanvasWidth(), textureProgram.getCanvasHeight(),
                    GLES20.GL_TEXTURE3);
            bufferList.add(effectOutput);
        }
    }

    @Override
    protected void realDraw(int firstVertex, int vertexCount, int i) {
        if (i == splittedBufferOrder.size() - 1 && isUsingToolKit) {
            effectOutput.bind();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            super.realDraw(firstVertex, vertexCount, i);
            effectOutput.unbind();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
        else{
            super.realDraw(firstVertex, vertexCount, i);
        }
    }

    public void setUsingToolKit(boolean usingToolKit) {
        isUsingToolKit = usingToolKit;
    }

    public int getEffectOutputTexture(){
        return effectOutput.getTexId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
