package nahuy.fithcmus.magiccam.presentation.commanders.impl.frame;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render.FrameImageRender;

/**
 * Created by huy on 6/1/2017.
 */

public class FrameCommander implements CallingAbstractCommander {

    private Frame frame;

    public FrameCommander(Frame frame) {
        this.frame = frame;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof FrameImageRender) {
            FrameImageRender frameImageRender = (FrameImageRender)obj;
            frameImageRender.changeFrame(frame);
        }
    }
}
