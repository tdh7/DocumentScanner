package nahuy.fithcmus.magiccam.domain.use_cases;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.managers.edit_item_reader.FrameReaderRepository;
import nahuy.fithcmus.magiccam.domain.mappers.DataToPresentMyFrameMapper;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;

/**
 * Created by huy on 6/20/2017.
 */

public class GettingFrameUseCase {
    public ArrayList<Frame> getAllFrames(){
        FrameReaderRepository frameReaderRepository = new FrameReaderRepository();
        ArrayList<DA_Frame> frames = frameReaderRepository.getAllFrame();
        ArrayList<Frame> results = new ArrayList<>();
        for(DA_Frame frame : frames){
            results.add(DataToPresentMyFrameMapper.fromDataFrame(frame));
        }
        return results;
    }
}
