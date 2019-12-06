package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl;

import java.nio.FloatBuffer;

/**
 * Created by huy on 6/14/2017.
 */

public interface TextureProgram{
    void setDefault(int program, float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride,
                    float[] texMatrix, FloatBuffer texBuffer, int texStride, int[] channelTexId);
    int getCanvasWidth();
    int getCanvasHeight();
}
