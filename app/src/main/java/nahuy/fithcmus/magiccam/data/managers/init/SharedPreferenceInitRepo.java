package nahuy.fithcmus.magiccam.data.managers.init;

import android.content.Context;

import nahuy.fithcmus.magiccam.data.clients.local.InitLocalAccessObject;
import nahuy.fithcmus.magiccam.domain.repositories.InitLoadingRepository;
import nahuy.fithcmus.magiccam.domain.use_cases.SyncLoadingInitUseCase;

/**
 * Created by huy on 6/18/2017.
 */

public class SharedPreferenceInitRepo implements InitLoadingRepository{
    @Override
    public boolean processLoading(Context context) {
        InitLocalAccessObject initLocalAccessObject = new InitLocalAccessObject();
        return initLocalAccessObject.checkIfDatabaseIsCreated(context);
    }
}
