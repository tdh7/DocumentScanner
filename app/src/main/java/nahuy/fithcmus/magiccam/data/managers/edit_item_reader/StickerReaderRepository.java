package nahuy.fithcmus.magiccam.data.managers.edit_item_reader;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.clients.database.frame.FrameAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.sticker.StickerAccessObject;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerReaderRepository {
    public ArrayList<DA_Sticker> getAllSticker(){
        return StickerAccessObject.getInstance().getAllStickers();
    }
}
