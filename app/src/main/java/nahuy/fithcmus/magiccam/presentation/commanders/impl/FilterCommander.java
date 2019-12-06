package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by nahuy on 5/24/2017.
 */

public class FilterCommander extends GLCommander {

    MyGLEffectShader se;

    public FilterCommander(MyGLEffectShader se) {
        super();
        this.se = se;
    }

    @Override
    public void process(Object obj) {
        if(checkType(obj)){
            MBRender render = cast(obj);
            render.changeFilterMode(se);
        }
    }

}
