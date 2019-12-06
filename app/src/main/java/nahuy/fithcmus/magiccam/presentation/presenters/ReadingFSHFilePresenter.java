package nahuy.fithcmus.magiccam.presentation.presenters;

import android.content.Context;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.use_cases.ReadFSHFileUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.GettingFSHFilterUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/18/2017.
 */

public class ReadingFSHFilePresenter {

    public static String readingFSHTextFile(Context context, String fileName){
        ReadFSHFileUseCase readFSHFileUseCase = new ReadFSHFileUseCase();
        return readFSHFileUseCase.readFshTextFile(context, fileName);
    }

    public ArrayList<MyGLEffectShader> getAllFilters(Context context){
        GettingFSHFilterUseCase readFSHFilterUseCase = new GettingFSHFilterUseCase();
        ArrayList<MyGLEffectShader> filters = readFSHFilterUseCase.getAllFilters();
        return filters;
    }

    public MyGLEffectShader getShaderByName(String shaderName){
        GettingFSHFilterUseCase readFSHFilterUseCase = new GettingFSHFilterUseCase();
        MyGLEffectShader shader = readFSHFilterUseCase.getShaderByName(shaderName);
        return shader;
    }

}
