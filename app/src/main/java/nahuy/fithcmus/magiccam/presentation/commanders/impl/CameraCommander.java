package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera.CameraSurfaceHandler;

public abstract class CameraCommander implements CallingAbstractCommander {

    protected CameraCommander(){}

    protected boolean checkType(Object obj){
        return obj instanceof CameraSurfaceHandler;
    }

    protected CameraSurfaceHandler cast(Object obj){
        return (CameraSurfaceHandler) obj;
    }

    @Override
    public abstract void process(Object obj);

}