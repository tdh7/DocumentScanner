package nahuy.fithcmus.magiccam.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huy on 6/23/2017.
 */

public class DA_User {
    @SerializedName("user_device_id")
    private String userId;

    public DA_User(String userId) {
        this.userId = userId;
    }

    public boolean isUserIdRight(String userId){
        return this.userId == userId;
    }
}
