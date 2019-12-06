package nahuy.fithcmus.magiccam.data.clients.api.service;

import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.data.entities.DA_User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by huy on 6/23/2017.
 */

public interface UserService {
    @GET("/devices/{user_device_id}")
    Call<DA_User> queryOrRegister(@Path("user_device_id") String device_id);
}
