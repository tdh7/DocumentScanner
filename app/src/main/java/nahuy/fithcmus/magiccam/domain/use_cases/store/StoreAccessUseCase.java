package nahuy.fithcmus.magiccam.domain.use_cases.store;

import nahuy.fithcmus.magiccam.data.entities.DA_User;
import nahuy.fithcmus.magiccam.data.managers.store.StoreQueryKeyRepoIml;
import nahuy.fithcmus.magiccam.domain.callbacks.GeneralUseCaseCallback;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreAccessUseCase implements StoreQueryKeyRepoIml.StoreRepoContract{

    GeneralUseCaseCallback resultUseCaseCallback;

    public StoreAccessUseCase(GeneralUseCaseCallback resultUseCaseCallback) {
        this.resultUseCaseCallback = resultUseCaseCallback;
    }

    public void queryDeviceKey(String key){
        StoreQueryKeyRepoIml storeQueryKeyRepoIml = new StoreQueryKeyRepoIml(this);
        storeQueryKeyRepoIml.queryKey(key);
    }

    @Override
    public void getQueryResult(DA_User user) {
        if(user != null){
            resultUseCaseCallback.onSuccess();
        }
        else{
            resultUseCaseCallback.onFail();
        }
    }
}
