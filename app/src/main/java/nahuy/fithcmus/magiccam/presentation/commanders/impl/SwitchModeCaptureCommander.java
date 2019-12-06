package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by nahuy on 5/24/2017.
 */

public class SwitchModeCaptureCommander extends GLCommander {

    public SwitchModeCaptureCommander() {
        super();
    }

    @Override
    public void process(Object obj) {
        if (checkType(obj)){
            MBRender render = cast(obj);
            render.switchModeCapture();
        }
    }
}
