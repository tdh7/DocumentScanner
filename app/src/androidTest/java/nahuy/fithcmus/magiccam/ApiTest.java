package nahuy.fithcmus.magiccam;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import nahuy.fithcmus.magiccam.data.clients.api.RetrofitClient;
import nahuy.fithcmus.magiccam.data.clients.api.service.UserService;
import nahuy.fithcmus.magiccam.data.entities.DA_User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

/**
 * Created by huy on 6/23/2017.
 */

@RunWith(AndroidJUnit4.class)
public class ApiTest {
    @Test
    public void testGetUser() throws Exception {
        UserService service = RetrofitClient.getClient().create(UserService.class);
        Callback<DA_User> callback = new Callback<DA_User>() {
            @Override
            public void onResponse(Call<DA_User> call, Response<DA_User> response) {
                assertTrue(response.body().isUserIdRight(6666));
            }

            @Override
            public void onFailure(Call<DA_User> call, Throwable t) {
                assertTrue(false);
            }
        };
        service.queryOrRegister(6666).enqueue(callback);

    }
}
