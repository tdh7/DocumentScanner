package nahuy.fithcmus.magiccam.data.managers.fsh_reader;

import android.content.Context;

import nahuy.fithcmus.magiccam.data.clients.local.ReadLocalFSHFile;
import nahuy.fithcmus.magiccam.domain.repositories.ReadFSHRepository;

/**
 * Created by huy on 6/18/2017.
 */

public class ReadFSHTextFileSyncRepo implements ReadFSHRepository
{
    @Override
    public String readFSHTextSync(Context context, String fileName) {
        return ReadLocalFSHFile.readLocalFSHTextFileSync(context, fileName);
    }
}
