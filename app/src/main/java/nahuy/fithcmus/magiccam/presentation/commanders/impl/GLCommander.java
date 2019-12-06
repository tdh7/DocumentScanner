package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

public abstract class GLCommander implements CallingAbstractCommander {

    protected GLCommander(){}

    protected boolean checkType(Object obj){
        return obj instanceof MBRender;
    }

    protected MBRender cast(Object obj){
        return (MBRender)obj;
    }

    @Override
    public abstract void process(Object obj);

}