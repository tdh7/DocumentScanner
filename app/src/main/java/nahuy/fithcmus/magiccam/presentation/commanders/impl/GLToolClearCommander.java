package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by huy on 6/20/2017.
 */

public class GLToolClearCommander extends GLCommander {
    @Override
    public void process(Object obj) {
        if(obj instanceof MBRender){
            MBRender mbRender = (MBRender)obj;
            mbRender.clearToolSurface();
        }
    }
}
