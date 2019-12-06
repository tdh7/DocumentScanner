package nahuy.fithcmus.magiccam.presentation.commanders.impl.filter;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.image_render.ImageRender;

/**
 * Created by huy on 6/21/2017.
 */

public class EditFilterCommander implements CallingAbstractCommander {
    private MyGLEffectShader myGLEffectShader;

    public EditFilterCommander(MyGLEffectShader myGLEffectShader) {
        this.myGLEffectShader = myGLEffectShader;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof ImageRender){
            ImageRender imgRender = (ImageRender)obj;
            imgRender.changeFilterMode(myGLEffectShader);
        }
    }
}
