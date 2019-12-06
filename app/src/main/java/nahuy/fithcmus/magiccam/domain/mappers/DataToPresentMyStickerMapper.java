package nahuy.fithcmus.magiccam.domain.mappers;

import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.entities.DA_StickerText;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;

/**
 * Created by huy on 6/20/2017.
 */

public class DataToPresentMyStickerMapper {
    public static Sticker fromDataSticker(DA_Sticker da_sticker){
        return new Sticker(da_sticker.getFilePath());
    }
    public static StickerText fromDataStickerText(DA_StickerText da_sticker){
        return new StickerText(da_sticker.getFilePath(), da_sticker.getColor());
    }
}
