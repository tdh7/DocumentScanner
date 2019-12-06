package nahuy.fithcmus.magiccam.domain.mappers;

import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterKernel;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.FilterKernel;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/18/2017.
 */

public class DataToPresentMyFilterMapper {
    public static MyGLEffectShader fromData(DA_MyGLShader da_myGLShader){
        return new MyGLEffectShader(da_myGLShader.getFilterName(),da_myGLShader.getFilterShader(),
                            da_myGLShader.getOrderCode(),da_myGLShader.getFilterImg(),null);
    }

    public static MyGLEffectShader fromDataPresent(DA_MyGLShader da_myGLShader){
        MyGLEffectShader myGLEffectShader = new
                MyGLEffectShader(da_myGLShader.getFilterName(),da_myGLShader.getFilterShader(), da_myGLShader.getFilterImg(),null);
        myGLEffectShader.setId(da_myGLShader.getId());
        myGLEffectShader.setDownloadLink(da_myGLShader.getDownloadLink());
        return myGLEffectShader;
    }

    public static FilterChannel fromDataChannel(DA_FilterChannel da_myChannel){
        return new FilterChannel(da_myChannel.getPath(),da_myChannel.getW(),
                da_myChannel.getH(),false);
    }

    public static FilterKernel fromDataKernel(DA_FilterKernel da_myKernel){
        return new FilterKernel(da_myKernel.getKernelId(), da_myKernel.getKernel());
    }
}
