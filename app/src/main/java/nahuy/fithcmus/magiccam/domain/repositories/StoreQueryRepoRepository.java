package nahuy.fithcmus.magiccam.domain.repositories;

/**
 * Created by huy on 6/24/2017.
 */

public interface StoreQueryRepoRepository {

    void onQuery(String key);

    public interface StoreQueryRepoResult{
        void onResult(Object object);
    }

}
