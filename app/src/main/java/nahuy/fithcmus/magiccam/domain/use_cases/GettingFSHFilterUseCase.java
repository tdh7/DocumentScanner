package nahuy.fithcmus.magiccam.domain.use_cases;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterKernel;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.data.managers.fsh_reader.ReadFSHFilterSyncRepo;
import nahuy.fithcmus.magiccam.domain.mappers.DataToPresentMyFilterMapper;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.MyGLShader;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by huy on 6/18/2017.
 */

public class GettingFSHFilterUseCase {

    public ArrayList<MyGLEffectShader> getAllFilters(){
        ReadFSHFilterSyncRepo readFSHFilterSyncRepo = new ReadFSHFilterSyncRepo();
        ArrayList<DA_MyGLShader> da_myGLShaders = readFSHFilterSyncRepo.readAllFilters();
        ArrayList<MyGLEffectShader> results = new ArrayList<>();
        for(DA_MyGLShader da_myGLShader : da_myGLShaders){
            MyGLEffectShader glShader = DataToPresentMyFilterMapper.fromData(da_myGLShader);
            if(da_myGLShader.isUsingKernel()){
                ArrayList<DA_FilterKernel> filterKernels = da_myGLShader.getKernel();
                for(DA_FilterKernel da_filterKernel : filterKernels){
                    glShader.addFilterKernel(DataToPresentMyFilterMapper.fromDataKernel(da_filterKernel));
                }
            }
            if(da_myGLShader.isUsingEChannel()){
                ArrayList<DA_FilterChannel> filterChannels = da_myGLShader.getFilterChannels();
                for(DA_FilterChannel da_filterChannel : filterChannels){
                    glShader.addFilterChannel(DataToPresentMyFilterMapper.fromDataChannel(da_filterChannel));
                }
            }
            results.add(glShader);
        }
        return results;
    }

    public MyGLEffectShader getShaderByName(String shaderName){
        ReadFSHFilterSyncRepo readFSHFilterSyncRepo = new ReadFSHFilterSyncRepo();
        DA_MyGLShader da_myGLShader = readFSHFilterSyncRepo.readFilterByName(shaderName);
        MyGLEffectShader glShader = DataToPresentMyFilterMapper.fromData(da_myGLShader);
        return glShader;
    }
}
