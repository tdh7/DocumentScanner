package nahuy.fithcmus.magiccam.data.clients.api.service;

import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FilterService {
 
   @GET("/devices/{device_id}/filters")
   Call<List<DA_MyGLShader>> getFiltes(@Path("device_id") String device_id);
     
   @GET("/devices/{device_id}/filter/{filter_id}")
   Call<DA_MyGLShader> getFilterById(@Path("device_id") String device_id, @Path("filter_id") int filter_id);
}