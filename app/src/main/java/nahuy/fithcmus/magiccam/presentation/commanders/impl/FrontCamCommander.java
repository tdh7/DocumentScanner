package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSwitchThread;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.camera.MainTopFragment;

/**
 * Created by nahuy on 5/24/2017.
 */

public class FrontCamCommander extends CameraCommander {

    public FrontCamCommander() {
        super();
    }

    @Override
    public void process(Object obj) {
        if(checkType(obj)){
            CameraSurfaceHandler cameraSurfaceHandler = cast(obj);
            CameraSwitchThread cameraThread = new CameraSwitchThread(cameraSurfaceHandler);
            cameraThread.execute(MainTopFragment.CAMERA_FRONT);
        }
    }

}
