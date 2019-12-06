package nahuy.fithcmus.magiccam.presentation.entities.shader_kit;

import android.content.Context;
import android.opengl.GLES20;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.FilterKernel;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.TextFileReader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.GlUtil;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.RenderBuffer;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.TextureProgram;


/**
 * Created by huy on 6/14/2017.
 */

public class MyGLShader implements Comparable, Serializable{
    private static Context context;
    private String shaderName;
    private String imgPath;
    private String fragmentShader;
    private ArrayList<FilterKernel> kernels;
    private ArrayList<FilterChannel> filterChannels;

    private static final String VERTEX_SHADER =
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord.xy;\n" +
                    "}\n";

    protected ArrayList<ArrayList<MyGLChannelIndex>> splittedBufferOrder;
    protected ArrayList<Integer> programList = new ArrayList<>();
    protected ArrayList<RenderBuffer> bufferList;

    public MyGLShader(String shaderName, String fShader, String imgPath, String... renderBuffer){
        this.imgPath = imgPath;
        initMyGLShader(shaderName, fShader, renderBuffer);
    }

    public MyGLShader(String shaderName, String fShader, String bufferOrder, String imgPath, String... renderBuffer){
        this.imgPath = imgPath;
        initMyGLShader(shaderName, fShader, renderBuffer);
        if(bufferOrder == null){
            return;
        }
        String[] splittedOrder = bufferOrder.split("\\|"); // Split from "B1,C1|B2,C2" -> ["B1,C1", "B2,C2"]
        splittedBufferOrder = new ArrayList<>(splittedOrder.length);
        for(String order : splittedOrder){
            String[] splittedOrderLevel2 = order.split(","); // Split to ["B1","C1"]
            ArrayList<MyGLChannelIndex> channelIndexes = new ArrayList<>();
            for(int i = 0; i < splittedOrderLevel2.length; i++){
                channelIndexes.add(new MyGLChannelIndex(splittedOrderLevel2[i]));
            }
            splittedBufferOrder.add(channelIndexes);
        }
    }

    String[] renderBuf;
    boolean firstTime = true;

    private void initMyGLShader(String shaderName, String fShader, String[] renderBuffer) {
        this.shaderName = shaderName;
        this.fragmentShader = fShader;
        this.renderBuf = renderBuffer;
    }

    public String getFragmentShader() {
        return TextFileReader.readTextFromLocalFile(context, fragmentShader);
    }

    public void addFilterChannel(FilterChannel added){
        if(filterChannels == null){
            filterChannels = new ArrayList<>();
        }
        filterChannels.add(added);
    }

    public void addFilterChannelOnly(FilterChannel added){
        if(filterChannels == null){
            filterChannels = new ArrayList<>();
        }
        else{
            filterChannels.clear();
        }
        filterChannels.add(added);
    }

    public void addFilterKernel(FilterKernel added){
        if(kernels == null){
            kernels = new ArrayList<>();
        }
        kernels.add(added);
    }

    public static void setContext(Context context) {
        MyGLShader.context = context;
    }

    public void release(){
        for(Integer programId : programList){
            GLES20.glDeleteProgram(programId);
        }

        if(programList != null) {
            programList.clear();
            programList = new ArrayList<>();
        }

        if(bufferList != null) {
            bufferList.clear();
            bufferList = null;
        }

        if(filterChannels != null){
            for(FilterChannel filterChannel : filterChannels){
                filterChannel.release();
            }
        }

        firstTime = true;
    }

    public void draw(TextureProgram textureProgram, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                     int vertexCount, int coordsPerVertex, int vertexStride,
                     float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride){

        if(firstTime){
            if(renderBuf != null) {
                if (renderBuf.length > 0) {
                    for (String rb : renderBuf) {
                        programList.add(GlUtil.createProgram(VERTEX_SHADER, TextFileReader.readTextFromLocalFile(context, rb)));
                    }
                }
            }
            programList.add(GlUtil.createProgram(VERTEX_SHADER, TextFileReader.readTextFromLocalFile(context, fragmentShader)));
            firstTime = false;
        }

        setUpBuffer(textureProgram);

        if(splittedBufferOrder != null){
            for(int i = 0; i < splittedBufferOrder.size(); i++){
                ArrayList<MyGLChannelIndex> channelIndices = splittedBufferOrder.get(i);
                int[] channelIDs = new int[channelIndices.size()];
                for(int j = 0; j < channelIndices.size(); j++){
                    switch (channelIndices.get(j).getChannelIndexType()){
                        case BUFFER:
                            channelIDs[j] = bufferList.get(channelIndices.get(j).getChannelId() - 1).getTexId();
                            break;
                        case MAIN:
                            channelIDs[j] = textureId;
                            break;
                        case FILTER:
                            channelIDs[j] = filterChannels.get(channelIndices.get(j).getChannelId() - 1).getTextureId(context,
                                    textureProgram.getCanvasWidth(), textureProgram.getCanvasHeight());
                            break;
                    }
                }
                setDefault(textureProgram, programList.get(i),
                        mvpMatrix, vertexBuffer, coordsPerVertex,
                        vertexStride, texMatrix, texBuffer,
                        texStride, channelIDs);
                realDraw(firstVertex, vertexCount, i);
            }
        }
        else {
            if (filterChannels != null) {
                int[] channelIDs = new int[filterChannels.size() + 1];
                for(int i = 0; i < channelIDs.length - 1; i++){
                    channelIDs[i] = filterChannels.get(i).getTextureId(context,
                            textureProgram.getCanvasWidth(), textureProgram.getCanvasHeight());
                }
                channelIDs[channelIDs.length - 1] = textureId;
                textureProgram.setDefault(programList.get(programList.size() - 1),
                        mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride,
                        texMatrix, texBuffer, texStride, channelIDs);
                // Draw the rect.
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
            }
            else{
                textureProgram.setDefault(programList.get(programList.size() - 1),
                        mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride,
                        texMatrix, texBuffer, texStride, new int[]{textureId});
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
            }
        }
    }

    protected void setUpBuffer(TextureProgram textureProgram) {
        if(bufferList == null && programList.size() > 1){
            bufferList = new ArrayList<>();
            for(int i = 0; i < programList.size() - 1; i++) {
                bufferList.add(new RenderBuffer(textureProgram.getCanvasWidth(),
                        textureProgram.getCanvasHeight(), GLES20.GL_TEXTURE5 + i));
            }
        }
    }

    protected void realDraw(int firstVertex, int vertexCount, int i) {
        if(i != splittedBufferOrder.size() - 1) {
            bufferList.get(i).bind();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
        if(i != splittedBufferOrder.size() - 1) {
            bufferList.get(i).unbind();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
    }

    protected void setDefault(TextureProgram textureProgram, int program,
                              float[] mvpMatrix, FloatBuffer vertexBuffer,
                              int coordsPerVertex, int vertexStride,
                              float[] texMatrix, FloatBuffer texBuffer,
                              int texStride, int[] channelTexId) {
        textureProgram.setDefault(program,
                mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride,
                texMatrix, texBuffer, texStride, channelTexId);
    }

    @Override
    public int compareTo(Object o) {
        MyGLShader se = (MyGLShader) o;
        String uniqueName = this.fragmentShader + this.shaderName;
        String uniqueName2 = se.fragmentShader + se.shaderName;
        return uniqueName.equals(uniqueName2) ? 1 : 0;
    }

    public void setAssetImageViewHeader(Context context, ImageView filter_main_img) {
        Glide.with(context).load(imgPath).into(filter_main_img);
    }

    public String getName() {
        return shaderName;
    }
}
