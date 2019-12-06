package nahuy.fithcmus.magiccam.domain.callbacks;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/24/2017.
 */

public interface StoreQueryCallback {
    void FilterResult(ArrayList<MyGLEffectShader> glEffectShaders);
    void FilterResultFail();
}
