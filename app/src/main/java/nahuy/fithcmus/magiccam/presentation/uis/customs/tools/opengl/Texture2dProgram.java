/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyDrawGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;

import static android.R.attr.x;
import static android.R.attr.y;


/**
 * GL program and supporting functions for textured 2D shapes.
 */
public class Texture2dProgram implements TextureProgram{
    private static final String TAG = GlUtil.TAG;

    public enum ProgramType {
        TEXTURE_2D, TEXTURE_EXT, TEXTURE_EXT_BW, TEXTURE_EXT_FILT
    }

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uTexMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "    gl_Position = uMVPMatrix * aPosition;\n" +
            "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
            "}\n";

    // Simple fragment shader for use with external 2D textures (e.g. what we get from
    // SurfaceTexture).

    private long START_TIME = System.currentTimeMillis();
    private ProgramType mProgramType;

    // Default program
    protected Drawable2d defaultDrawable = new Drawable2d(Drawable2d.Prefab.DEFAULT_FULL_RECTANGLE);
    private int DEFAULT_PROGRAM;

    // Handles to the GL program and various components of it.
    private int mProgramHandle;
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int muKernelLoc;
    private int muTexOffsetLoc;
    private int muColorAdjustLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;
    private int iGlobalTimeLocation;
    private int iResolution;
    private int iMouse;
    protected int iFrame = 0;

    private FloatBuffer resolutionBuf;

    private int mDefaultTextureTarget;
    private int mTextureTarget;
    
    private static final int KERNEL_SIZE = 9;

    private float[] mKernel = new float[KERNEL_SIZE];
    private float[] mTexOffset;
    private float mColorAdjust;
    
    private String vss = VERTEX_SHADER;
    protected Context context;

    private float[] mousePosition = new float[4];

    protected MyGLEffectShader effectShader;
    private MyToolGLShader toolShader;

    public Texture2dProgram() {
    }

    public Texture2dProgram(Context context) {

        this.context = context;

        // Create default program
        initProgram();

        // effectShader = new MyGLShader("Huy", "free_draw.fsh", "B1|B1,M1", new String[]{"free_draw_buf_a.fsh"});
        // effectShader = new MyGLEffectShader("", "normal.fsh", "M1", "", new String[]{});
        // effectShader.addFilterChannel(new FilterChannel("lut1.png", 0, 0, false));
        // toolShader = new MyDrawGLShader();
        // Create level 2 shader program
        // changeProgram();
        // get locations of attributes and uniforms

    }

	private void findAttr() {
        //iResolution = GLES20.glGetAttribLocation(mProgramHandle, "aResolutions");
        //GlUtil.checkLocation(iResolution, "aResolutions");
		maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
        // iGlobalTimeLocation = GLES20.glGetUniformLocation(mProgramHandle, "iGlobalTime");
        // GlUtil.checkLocation(iGlobalTimeLocation, "iGlobalTime");
	}

	private void initProgram() {
		mDefaultTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

        MyGLShader shaderEffect = new MyGLShader("Normal", Constants.DEFAULT_FRAGMENT_ORIGIN_SHADER, "",  null);
		
		DEFAULT_PROGRAM = GlUtil.createProgram(vss, shaderEffect.getFragmentShader());
        
        if (DEFAULT_PROGRAM == 0) {
            throw new RuntimeException("Unable to create program");
        }
        
        Log.d(TAG, "Created program " + DEFAULT_PROGRAM);
	}

    private void changeProgram(){
        mTextureTarget = GLES20.GL_TEXTURE_2D;

        // mProgramHandle = GlUtil.createProgram(vss, fsh.getFragmentShader());

        if (mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }

        Log.d(TAG, "Created program " + mProgramHandle);
    }
    
    public void changeFS(MyGLEffectShader glShader){
        if(effectShader != null){
            effectShader.release();
        }
        effectShader = glShader;
        resetMouse();
    }

    public void changeTool(MyToolGLShader mNewTool) {
        if(toolShader == null){
            toolShader = mNewTool;
            return;
        }
        toolShader.release();
        toolShader = mNewTool;
    }

    /**
     * Releases the program.
     * <p>
     * The appropriate EGL context must be current (i.e. the one that was used to create
     * the program).
     */
    public void release() {
        if(effectShader != null) {
            effectShader.release();
        }
        if(toolShader != null){
            toolShader.release();
        }
        resetMouse();
    }

    /**
     * Returns the program type.
     */
    public ProgramType getProgramType() {
        return mProgramType;
    }

    /**
     * Creates a texture object suitable for use with this program.
     * <p>
     * On exit, the texture will be bound.
     */
    public int createTextureObject() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GlUtil.checkGlError("glGenTextures");

        int texId = textures[0];
        GLES20.glBindTexture(mDefaultTextureTarget, texId);
        GlUtil.checkGlError("glBindTexture " + texId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");

        return texId;
    }

    /**
     * Configures the convolution filter values.
     *
     * @param values Normalized filter values; must be KERNEL_SIZE elements.
     */
    public void setKernel(float[] values, float colorAdj) {
        if (values.length != KERNEL_SIZE) {
            throw new IllegalArgumentException("Kernel size is " + values.length +
                    " vs. " + KERNEL_SIZE);
        }
        System.arraycopy(values, 0, mKernel, 0, KERNEL_SIZE);
        mColorAdjust = colorAdj;
        //Log.d(TAG, "filt kernel: " + Arrays.toString(mKernel) + ", adj=" + colorAdj);
    }

    public int[] createTextureObject(Context context, int resId){
        int[] texId = GlUtil.loadTexture(context, resId);
        return texId;
    }

    /**
     * Sets the size of the texture.  This is used to find adjacent texels when filtering.
     */
    float[] resoArr;
    public void setTexSize(int width, int height) {
        float rw = 1.0f / width;
        float rh = 1.0f / height;
        
        resoArr = new float[]{width, height};
        // resolutionBuf = GlUtil.createFloatBuffer(resoArr);

        renderBuffer = new RenderBuffer((int)resoArr[0], (int)resoArr[1], BUF_ACTIVE_TEX_UNIT);

        // Don't need to create a new array here, but it's syntactically convenient.
        mTexOffset = new float[] {
            -rw, -rh,   0f, -rh,    rw, -rh,
            -rw, 0f,    0f, 0f,     rw, 0f,
            -rw, rh,    0f, rh,     rw, rh
        };
        //Log.d(TAG, "filt size: " + width + "x" + height + ": " + Arrays.toString(mTexOffset));
    }

    private RenderBuffer renderBuffer;
    private static final int BUF_ACTIVE_TEX_UNIT = GLES20.GL_TEXTURE8;

    public int drawDefault(int textureId, int canvasWidth, int canvasHeight,
                           float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                           int vertexCount, int coordsPerVertex, int vertexStride,
                           float[] texMatrix, FloatBuffer texBuffer, int texStride){

        GlUtil.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(DEFAULT_PROGRAM);
        GlUtil.checkGlError("glUseProgram");

        int sT = GLES20.glGetUniformLocation(DEFAULT_PROGRAM, "sTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(mDefaultTextureTarget, textureId);
        GLES20.glUniform1i(sT, 0);

        muMVPMatrixLoc = GLES20.glGetUniformLocation(DEFAULT_PROGRAM, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("uMVPMatrix");

        muTexMatrixLoc = GLES20.glGetUniformLocation(DEFAULT_PROGRAM, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("uTexMatrix");


        maPositionLoc = GLES20.glGetAttribLocation(DEFAULT_PROGRAM, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, defaultDrawable.getCoordsPerVertex(),
                GLES20.GL_FLOAT, false, defaultDrawable.getVertexStride(), defaultDrawable.getVertexArray());
        /*GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);*/
        GlUtil.checkGlError("glVertexAttribPointer");

        maTextureCoordLoc = GLES20.glGetAttribLocation(DEFAULT_PROGRAM, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, defaultDrawable.getTexCoordStride(), defaultDrawable.getTexCoordArray());
        GlUtil.checkGlError("glVertexAttribPointer");

        renderBuffer.bind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, defaultDrawable.getVertexCount());
        GlUtil.checkGlError("glDrawArrays");
        renderBuffer.unbind();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Done -- disable vertex array, texture, and program.
        cleanShaderAfterUsing();

        return renderBuffer.getTexId();
    }

    protected void cleanShaderAfterUsing() {
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);

        GLES20.glUseProgram(0);
    }

    /**
     * Issues the draw call.  Does the full setup on every call.
     *
     * @param mvpMatrix The 4x4 projection matrix.
     * @param vertexBuffer Buffer with vertex position data.
     * @param firstVertex Index of first vertex to use in vertexBuffer.
     * @param vertexCount Number of vertices in vertexBuffer.
     * @param coordsPerVertex The number of coordinates per vertex (e.g. x,y is 2).
     * @param vertexStride Width, in bytes, of the position data for each vertex (often
     *        vertexCount * sizeof(float)).
     * @param texMatrix A 4x4 transformation matrix for texture coords.  (Primarily intended
     *        for use with SurfaceTexture.)
     * @param texBuffer Buffer with vertex texture data.
     * @param texStride Width, in bytes, of the texture data for each vertex.
     */
    public void draw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
            int vertexCount, int coordsPerVertex, int vertexStride,
            float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {

        int normalizeTexId = drawDefault(textureId, (int)resoArr[0], (int)resoArr[1],
                mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex,
                vertexStride, texMatrix, texBuffer, texStride);

        iFrame++;

        if(toolShader != null){
            effectShader.setUsingToolKit(true);
        }

        if(effectShader != null) {
            effectShader.draw(this, mvpMatrix.clone(), vertexBuffer, firstVertex, vertexCount,
                    coordsPerVertex, vertexStride, texMatrix, texBuffer, normalizeTexId, texStride);
        }

        cleanShaderAfterUsing();

        if(toolShader != null){
            toolShader.draw(this, mvpMatrix.clone(), vertexBuffer, firstVertex, vertexCount,
                    coordsPerVertex, vertexStride, texMatrix, texBuffer, effectShader.getEffectOutputTexture()
                   , texStride);
        }

        cleanShaderAfterUsing();
    }

    private Boolean mousePressed = null;

    private void resetMouse(){
        if(resoArr != null) {
            mousePosition[0] = resoArr[0] * 0.5f;
            mousePosition[1] = resoArr[1] * 0.5f;
        }
        else{
            mousePosition[0] = 0.0f;
            mousePosition[1] = 0.0f;
        }
        mousePosition[2] = 0.0f;
        mousePosition[3] = 0.0f;

    }

    long moveChange = 0;
    int downChange = 0;

    public void changeMouse(float x, float y){
        mousePosition[0] = x;
        if(resoArr != null) {
            mousePosition[1] = resoArr[1] - y;
        }
        else{
            mousePosition[1] = 0.0f;
        }
        mousePressed = true;
        moveChange++;
        Log.i("NAHUY_DOWN", moveChange + "_" + mousePosition[0] + ":" + mousePosition[1]);
    }

    public void changeMouseDown(float x, float y) {
        mousePosition[2] = x;
        if(resoArr != null) {
            mousePosition[3] = resoArr[1] - y;
        }
        else{
            mousePosition[3] = 0.0f;
        }
        downChange++;
        Log.i("NAHUY_DOWN", downChange + "_" + mousePosition[2] + ":" + mousePosition[3]);
    }

    public void mouseUp() {
        mousePosition[2] = 0.0f;
        mousePosition[3] = 0.0f;
    }

    @Override
    public int getCanvasWidth() {
        return (int)resoArr[0];
    }

    @Override
    public int getCanvasHeight() {
        return (int)resoArr[1];
    }

    @Override
    public void setDefault(int program, float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride,
                           float[] texMatrix, FloatBuffer texBuffer, int texStride, int[] channelTexId) {

        GlUtil.checkGlError("draw start");
        GLES20.glUseProgram(program);
        GlUtil.checkGlError("use program");

        // Set global time.
        iGlobalTimeLocation = GLES20.glGetUniformLocation(program, "iGlobalTime");
        float time = ((float) (System.currentTimeMillis() - START_TIME)) / 1000.0f;
        if( time > 30 ){
            START_TIME = System.currentTimeMillis() - 1000;
            time = ((float) (System.currentTimeMillis() - START_TIME)) / 1000.0f;
        }
        GLES20.glUniform1f(iGlobalTimeLocation, time);
        GlUtil.checkGlError("iGlobalTime");

        int iFrameLocation = GLES20.glGetUniformLocation(program, "iFrame");
        GLES20.glUniform1i(iFrameLocation, iFrame);

        // Set resolution.
        iResolution = GLES20.glGetUniformLocation(program, "iResolution");
        GLES20.glUniform2fv(iResolution, 1, FloatBuffer.wrap(new float[]{resoArr[0], resoArr[1]}));
        GlUtil.checkGlError("iResolution");

        // Set mouse event
        iMouse = GLES20.glGetUniformLocation(program, "iMouse");
        GLES20.glUniform4fv(iMouse, 1, FloatBuffer.wrap(new float[]{mousePosition[0], mousePosition[1],
                mousePosition[2], mousePosition[3]}));
        GlUtil.checkGlError("iMouse");

        // Draw camera channel
        for(int i = 0; i < channelTexId.length; i++){
            String name = "iChannel" + Integer.toString(i);
            int sST = GLES20.glGetUniformLocation(program, name);
            // int channelTexId = filterChannels.get(i).getTextureId(context, (int)resoArr[0], (int)resoArr[1]);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, channelTexId[i]);
            GLES20.glUniform1i(sST, i);
            GlUtil.checkGlError(name);
        }

        /*int tmpMVPMatrixLoc = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GlUtil.checkLocation(tmpMVPMatrixLoc, "uMVPMatrix");
        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(program, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        muTexMatrixLoc = GLES20.glGetUniformLocation(program, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");*/

        maPositionLoc = GLES20.glGetAttribLocation(program, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("aPosition");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, defaultDrawable.getCoordsPerVertex(),
                GLES20.GL_FLOAT, false, defaultDrawable.getVertexStride(), defaultDrawable.getVertexArray());
        /*GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);*/
        GlUtil.checkGlError("glVertexAttribPointer");

        maTextureCoordLoc = GLES20.glGetAttribLocation(program, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, defaultDrawable.getTexCoordStride(), defaultDrawable.getTexCoordArray());
        GlUtil.checkGlError("glVertexAttribPointer");

    }

    /*// Select the program.
    GLES20.glUseProgram(mProgramHandle);
    GlUtil.checkGlError("glUseProgram");

    // Set global time.
    if(fsh.isUsingTime()){
        iGlobalTimeLocation = GLES20.glGetUniformLocation(mProgramHandle, "uGlobalTime");
        GlUtil.checkLocation(iGlobalTimeLocation, "uGlobalTime");
        float time = ((float) (System.currentTimeMillis() - START_TIME)) / 1000.0f;
        if( time > 30 ){
            START_TIME = System.currentTimeMillis() - 1000;
            time = ((float) (System.currentTimeMillis() - START_TIME)) / 1000.0f;
        }
        GLES20.glUniform1f(iGlobalTimeLocation, time);
    }

    // Set resolution.
    if(fsh.isUsingResolution()){
        iResolution = GLES20.glGetUniformLocation(mProgramHandle, "uResolution");
        GlUtil.checkLocation(iResolution, "uResolution");
        GLES20.glUniform2fv(iResolution, 1, FloatBuffer.wrap(new float[]{resoArr[0], resoArr[1]}));
    }

    // Set kernel.
    if(fsh.isUsingKernel()){
        int kernelLoc = GLES20.glGetUniformLocation(mProgramHandle, "kernel");
        GLES20.glUniform1fv(kernelLoc, fsh.getKernel().length, fsh.getKernel(), 0);
    }

    // Set mouse event
    if(fsh.isUsingMouse()){
        if(mousePressed == null){
            resetMouse();
            mousePressed = true;
        }
        iMouse = GLES20.glGetUniformLocation(mProgramHandle, "uMouse");
        GlUtil.checkLocation(iMouse, "uMouse");
        GLES20.glUniform2fv(iMouse, 1, FloatBuffer.wrap(new float[]{mousePosition[0], mousePosition[1]}));
    }
    muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, texStride, texBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(mTextureTarget, 0);
        GLES20.glUseProgram(0);
    */
}
