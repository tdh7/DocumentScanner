package nahuy.fithcmus.magiccam.data.managers.store;

import java.util.List;

import nahuy.fithcmus.magiccam.data.clients.api.RetrofitClient;
import nahuy.fithcmus.magiccam.data.clients.api.service.FilterService;
import nahuy.fithcmus.magiccam.data.clients.api.service.UserService;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.data.entities.DA_User;
import nahuy.fithcmus.magiccam.domain.repositories.StoreQueryRepoRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreQueryFilterRepoIml implements StoreQueryRepoRepository{

    StoreQueryRepoRepository.StoreQueryRepoResult queryRepoResult;

    public StoreQueryFilterRepoIml(StoreQueryRepoRepository.StoreQueryRepoResult queryRepoResult) {
        this.queryRepoResult = queryRepoResult;
    }

    @Override
    public void onQuery(String key) {
        FilterService service = RetrofitClient.getClient().create(FilterService.class);
        Callback<List<DA_MyGLShader>> filterCallback = new Callback<List<DA_MyGLShader>>() {
            @Override
            public void onResponse(Call<List<DA_MyGLShader>> call, Response<List<DA_MyGLShader>> response) {
                queryRepoResult.onResult(response.body());
            }

            @Override
            public void onFailure(Call<List<DA_MyGLShader>> call, Throwable t) {
                queryRepoResult.onResult(null);
            }
        };
        service.getFiltes(key).enqueue(filterCallback);
    }
}
