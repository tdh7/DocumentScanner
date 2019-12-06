package nahuy.fithcmus.magiccam.data.managers.store;

import nahuy.fithcmus.magiccam.data.clients.api.RetrofitClient;
import nahuy.fithcmus.magiccam.data.clients.api.service.UserService;
import nahuy.fithcmus.magiccam.data.entities.DA_User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.id;
import static junit.framework.Assert.assertTrue;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreQueryKeyRepoIml {

    StoreRepoContract storeRepoContract;

    public StoreQueryKeyRepoIml(StoreRepoContract storeRepoContract) {
        this.storeRepoContract = storeRepoContract;
    }

    public void queryKey(String key){
        UserService service = RetrofitClient.getClient().create(UserService.class);
        Callback<DA_User> callback = new Callback<DA_User>() {
            @Override
            public void onResponse(Call<DA_User> call, Response<DA_User> response) {
                storeRepoContract.getQueryResult(response.body());
            }

            @Override
            public void onFailure(Call<DA_User> call, Throwable t) {
                storeRepoContract.getQueryResult(null);
            }
        };
        service.queryOrRegister(key).enqueue(callback);
    }

    // Implemented by use case to get result
    public interface StoreRepoContract{
        void getQueryResult(DA_User user);
    }
}
