package nahuy.fithcmus.magiccam.data.clients.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huy on 6/23/2017.
 */

public class RetrofitClient {

    private static String baseUrl = "http://nguyenhuy3588.pythonanywhere.com";
    private static Retrofit retrofit = null;

    private RetrofitClient(){}

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
