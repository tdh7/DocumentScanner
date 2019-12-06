package nahuy.fithcmus.magiccam.domain.use_cases;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.managers.edit_item_reader.StickerReaderRepository;
import nahuy.fithcmus.magiccam.data.managers.edit_item_reader.StickerReaderRepository;
import nahuy.fithcmus.magiccam.domain.mappers.DataToPresentMyStickerMapper;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;

/**
 * Created by huy on 6/20/2017.
 */

public class GettingStickerUseCase {
    public ArrayList<Sticker> getAllStickers(){
        StickerReaderRepository stickerReaderRepository = new StickerReaderRepository();
        ArrayList<DA_Sticker> stickers = stickerReaderRepository.getAllSticker();
        ArrayList<Sticker> results = new ArrayList<>();
        for(DA_Sticker sticker : stickers){
            results.add(DataToPresentMyStickerMapper.fromDataSticker(sticker));
        }
        return results;
    }
}
