package nahuy.fithcmus.magiccam.domain.mappers;

import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;

/**
 * Created by huy on 6/20/2017.
 */

public class DataToPresentMyFrameMapper {
    public static Frame fromDataFrame(DA_Frame da_frame){
        return new Frame(da_frame.getFilePath());
    }
}
