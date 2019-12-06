package nahuy.fithcmus.magiccam.domain.repositories;

import android.content.Context;

/**
 * Created by huy on 6/18/2017.
 */

public interface ReadFSHRepository {
    String readFSHTextSync(Context context, String fileName);
}
