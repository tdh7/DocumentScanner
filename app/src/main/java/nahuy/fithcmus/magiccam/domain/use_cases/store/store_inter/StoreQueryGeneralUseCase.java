package nahuy.fithcmus.magiccam.domain.use_cases.store.store_inter;

import nahuy.fithcmus.magiccam.domain.repositories.StoreQueryRepoRepository;

/**
 * Created by huy on 6/24/2017.
 */

public interface StoreQueryGeneralUseCase extends StoreQueryRepoRepository.StoreQueryRepoResult{
    void onQuery(String key);
}
