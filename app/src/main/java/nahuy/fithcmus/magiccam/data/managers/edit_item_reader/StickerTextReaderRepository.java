package nahuy.fithcmus.magiccam.data.managers.edit_item_reader;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.clients.database.frame.FrameAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.sticker_text.StickerTextAccessObject;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.entities.DA_StickerText;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerTextReaderRepository {
    public ArrayList<DA_StickerText> getAllSticker(){
        return StickerTextAccessObject.getInstance().getAllStickers();
    }
}
