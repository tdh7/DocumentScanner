package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by huy on 6/20/2017.
 */

public class GlColorCommander extends GLCommander {

    private float[] colors = new float[3];

    public GlColorCommander(int r, int g, int b) {
        colors[0] = (float)r / 255.0f;
        colors[1] = (float)g / 255.0f;
        colors[2] = (float)b / 255.0f;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof MBRender){
            MBRender mbRender = (MBRender)obj;
            mbRender.changeToolColor(colors);
        }
    }
}
