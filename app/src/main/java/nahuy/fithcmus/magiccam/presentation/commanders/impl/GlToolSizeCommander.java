package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by huy on 6/20/2017.
 */

public class GlToolSizeCommander extends GLCommander {

    private float size;
    public GlToolSizeCommander(float size) {
        this.size = size;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof MBRender){
            MBRender render = (MBRender)obj;
            render.changeToolSize(size);
        }
    }
}
