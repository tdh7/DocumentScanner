package nahuy.fithcmus.magiccam.presentation.presenters;

import android.content.Context;

import nahuy.fithcmus.magiccam.domain.use_cases.SyncLoadingInitUseCase;

/**
 * Created by huy on 6/18/2017.
 */

public class LocalSyncLoadingPresenter {

    private static LocalSyncLoadingPresenter localSyncLoadingPresenter = new LocalSyncLoadingPresenter();

    private LocalSyncLoadingPresenter(){}

    public static LocalSyncLoadingPresenter getInstance(){
        return localSyncLoadingPresenter;
    }

    public boolean copyAssets(Context context){
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        return syncLoadingInitUseCase.initLoadAssets(context);
    }

    public boolean copyDatabase(Context context){
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        return syncLoadingInitUseCase.initLoadDatabase(context);
    }

    public boolean checkIfAlreadyInit(Context context) {
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        return syncLoadingInitUseCase.checkIfAlreadyInit(context);
    }

    public boolean loadDataInDatabase(Context context) {
        SyncLoadingInitUseCase syncLoadingInitUseCase = new SyncLoadingInitUseCase();
        return syncLoadingInitUseCase.initDataInDatabase(context);
    }
}
