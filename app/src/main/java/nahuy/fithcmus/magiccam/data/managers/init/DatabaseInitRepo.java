package nahuy.fithcmus.magiccam.data.managers.init;

import android.content.Context;

import nahuy.fithcmus.magiccam.data.clients.local.InitLocalAccessObject;
import nahuy.fithcmus.magiccam.domain.repositories.InitLoadingRepository;

/**
 * Created by huy on 6/18/2017.
 */

public class DatabaseInitRepo implements InitLoadingRepository {
    @Override
    public boolean processLoading(Context context) {
        InitLocalAccessObject initLocalAccessObject = new InitLocalAccessObject();
        return initLocalAccessObject.loadingDatabaseFromAssetFolder(context);
    }
}
