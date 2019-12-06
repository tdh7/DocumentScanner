package nahuy.fithcmus.magiccam.domain.use_cases;

import android.content.Context;

import nahuy.fithcmus.magiccam.data.managers.fsh_reader.ReadFSHTextFileSyncRepo;
import nahuy.fithcmus.magiccam.domain.repositories.ReadFSHRepository;

/**
 * Created by huy on 6/18/2017.
 */

public class ReadFSHFileUseCase {
    public String readFshTextFile(Context context, String fileName){
        ReadFSHRepository readFshRepository = new ReadFSHTextFileSyncRepo();
        return readFshRepository.readFSHTextSync(context, fileName);
    }
}
