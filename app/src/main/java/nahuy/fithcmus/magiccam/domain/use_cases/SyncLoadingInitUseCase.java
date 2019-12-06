package nahuy.fithcmus.magiccam.domain.use_cases;

import android.content.Context;

import nahuy.fithcmus.magiccam.data.managers.init.DatabaseInitRepo;
import nahuy.fithcmus.magiccam.data.managers.init.InitDatabaseDataRepo;
import nahuy.fithcmus.magiccam.data.managers.init.InitFilesInitRepo;
import nahuy.fithcmus.magiccam.data.managers.init.SharedPreferenceInitRepo;
import nahuy.fithcmus.magiccam.domain.repositories.InitLoadingRepository;

/**
 * Created by huy on 6/18/2017.
 */

public class SyncLoadingInitUseCase {

    public boolean initLoadAssets(Context context){
        InitLoadingRepository initLoadingRepository = new InitFilesInitRepo();
        return initLoadingRepository.processLoading(context);
    }

    public boolean initLoadDatabase(Context context){
        InitLoadingRepository initLoadingRepository = new DatabaseInitRepo();
        return initLoadingRepository.processLoading(context);
    }

    public boolean checkIfAlreadyInit(Context context){
        InitLoadingRepository initLoadingRepository = new SharedPreferenceInitRepo();
        return initLoadingRepository.processLoading(context);
    }

    public boolean initDataInDatabase(Context context){
        InitLoadingRepository initLoadingRepository = new InitDatabaseDataRepo();
        return initLoadingRepository.processLoading(context);
    }

}
