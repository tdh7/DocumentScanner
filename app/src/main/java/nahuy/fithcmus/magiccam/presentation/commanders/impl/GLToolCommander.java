package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.MBRender;

/**
 * Created by huy on 6/19/2017.
 */

public class GLToolCommander extends GLCommander {
    MyToolGLShader se;

    public GLToolCommander(MyToolGLShader se) {
        super();
        this.se = se;
    }

    @Override
    public void process(Object obj) {
        if(checkType(obj)){
            MBRender render = cast(obj);
            render.changeTool(se);
        }
    }
}
