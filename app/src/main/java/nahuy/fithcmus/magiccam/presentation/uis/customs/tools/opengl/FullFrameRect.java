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
import android.graphics.Bitmap;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;

import static android.R.attr.filter;
import static nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.GlUtil.loadTextureFromBitmap;

/**
 * This class essentially represents a viewport-sized sprite that will be rendered with
 * a texture, usually from an external source like the camera or video decoder.
 */
public class FullFrameRect {
    private final Drawable2d mBRectDrawable = new Drawable2d(Drawable2d.Prefab.BACK_FULL_RECTANGLE);
    private final Drawable2d mFRectDrawable = new Drawable2d(Drawable2d.Prefab.FRONT_FULL_RECTANGLE);
    private Drawable2d mRectDrawable = mBRectDrawable;
    private Texture2dProgram mProgram;
    private Context context;

    /**
     * Prepares the object.
     *
     * @param context The program to use.  FullFrameRect takes ownership, and will release
     *     the program when no longer needed.
     */
    public FullFrameRect(Context context) {
        this.context = context;
        mProgram = new Texture2dProgram(this.context);
    }

    public FullFrameRect(Context context, int flagImage) {
        this.context = context;
        mProgram = new Texture2DImage(this.context);
    }
    
    public FullFrameRect(Texture2dProgram program) {
        mProgram = program;
    }

    /**
     * Releases resources.
     * <p>
     * This must be called with the appropriate EGL context current (i.e. the one that was
     * current when the constructor was called).  If we're about to destroy the EGL context,
     * there's no value in having the caller make it current just to do this cleanup, so you
     * can pass a flag that will tell this function to skip any EGL-context-specific cleanup.
     */
    public void release(boolean doEglCleanup) {
        if (mProgram != null) {
            if (doEglCleanup) {
                mProgram.release();
            }
            mProgram = null;
        }
    }

    /**
     * Returns the program currently in use.
     */
    public Texture2dProgram getProgram() {
        return mProgram;
    }

    /**
     * Changes the program.  The previous program will be released.
     * <p>
     * The appropriate EGL context must be current.
     */
    public void changeProgram(Texture2dProgram program) {
        mProgram.release();
        mProgram = program;
    }
    
    public void changeFilter(MyGLEffectShader filter){
    	mProgram.changeFS(filter);
    }

    public void changeTool(MyToolGLShader mNewTool) {
        mProgram.changeTool(mNewTool);
    }

    /**
     * Creates a texture object suitable for use with drawFrame().
     */
    public int createTextureObject() {
        return mProgram.createTextureObject();
    }

    public int createTextureObject(Context context, Bitmap bm) {
        int texID = GlUtil.loadTextureFromBitmap(context, bm);
        mProgram.setTexSize(bm.getWidth(), bm.getHeight());
        return texID;
    }

    public int[] createTextureObject(Context context, int resId) {
        return mProgram.createTextureObject(context, resId);
    }

    public void changeFace(){
        if(mRectDrawable == mBRectDrawable){
            mRectDrawable = mFRectDrawable;
        }
        else{
            mRectDrawable = mBRectDrawable;
        }
    }

    public void mouseMove(float x, float y){
        mProgram.changeMouse(x, y);
    }

    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId, float[] texMatrix) {
        // Use the identity matrix for MVP so our 2x2 BACK_FULL_RECTANGLE covers the viewport.
        mProgram.draw(GlUtil.IDENTITY_MATRIX, mRectDrawable.getVertexArray(), 0,
                mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(),
                texMatrix, mRectDrawable.getTexCoordArray(), textureId,
                mRectDrawable.getTexCoordStride());
    }

    public void mouseDown(float x, float y) {
        mProgram.changeMouseDown(x, y);
    }

    public int createTextureObjectFromImagePath(Context context, String imgPath) {
        return GlUtil.loadTextureFromPath(context, imgPath, 0, 0);
    }

    public int createTextureObjectFromBitmap(Context context, Bitmap imgPath) {
        return loadTextureFromBitmap(context, imgPath);
    }

    public void mouseUp() {
        mProgram.mouseUp();
    }
}
