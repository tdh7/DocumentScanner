package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.gl_action;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huy on 6/21/2017.
 */

public interface MBRenderAction {
    void action();
    void performAction(GL10 gl);
    void updateFilter();
    void updateTool();
    void updatePSize();
}
