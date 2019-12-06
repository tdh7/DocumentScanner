package nahuy.fithcmus.magiccam.domain.use_cases.store;

import java.util.ArrayList;
import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterKernel;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.data.managers.store.StoreQueryFilterRepoIml;
import nahuy.fithcmus.magiccam.domain.callbacks.StoreQueryCallback;
import nahuy.fithcmus.magiccam.domain.mappers.DataToPresentMyFilterMapper;
import nahuy.fithcmus.magiccam.domain.repositories.StoreQueryRepoRepository;
import nahuy.fithcmus.magiccam.domain.use_cases.store.store_inter.StoreQueryGeneralUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreFilterQueryUseCase implements StoreQueryGeneralUseCase {

    private StoreQueryCallback storeQueryCallback;

    public StoreFilterQueryUseCase(StoreQueryCallback storeQueryCallback) {
        this.storeQueryCallback = storeQueryCallback;
    }

    @Override
    public void onQuery(String key) {
        StoreQueryRepoRepository storeQueryRepoRepository = new StoreQueryFilterRepoIml(this);
        storeQueryRepoRepository.onQuery(key);
    }

    @Override
    public void onResult(Object object) {
        if(object == null){
            storeQueryCallback.FilterResultFail();
        }
        else{
            List<DA_MyGLShader> myGLShaders = (List<DA_MyGLShader>)object;
            ArrayList<MyGLEffectShader> results = new ArrayList<>();
            for(DA_MyGLShader da_myGLShader : myGLShaders){
                MyGLEffectShader glShader = DataToPresentMyFilterMapper.fromDataPresent(da_myGLShader);
                results.add(glShader);
            }
            storeQueryCallback.FilterResult(results);
        }
    }
}
