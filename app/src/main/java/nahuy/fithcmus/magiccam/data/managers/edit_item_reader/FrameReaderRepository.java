package nahuy.fithcmus.magiccam.data.managers.edit_item_reader;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.clients.database.frame.FrameAccessObject;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;

/**
 * Created by huy on 6/20/2017.
 */

public class FrameReaderRepository {
    public ArrayList<DA_Frame> getAllFrame(){
        return FrameAccessObject.getInstance().getAllFrames();
    }
}
