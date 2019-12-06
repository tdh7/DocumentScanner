package nahuy.fithcmus.magiccam.domain.use_cases;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.entities.DA_StickerText;
import nahuy.fithcmus.magiccam.data.managers.edit_item_reader.StickerTextReaderRepository;
import nahuy.fithcmus.magiccam.domain.mappers.DataToPresentMyStickerMapper;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;

/**
 * Created by huy on 6/20/2017.
 */

public class GettingStickerTextUseCase {
    public ArrayList<StickerText> getAllStickerTexts(){
        StickerTextReaderRepository stickerReaderRepository = new StickerTextReaderRepository();
        ArrayList<DA_StickerText> stickers = stickerReaderRepository.getAllSticker();
        ArrayList<StickerText> results = new ArrayList<>();
        for(DA_StickerText sticker : stickers){
            results.add(DataToPresentMyStickerMapper.fromDataStickerText(sticker));
        }
        return results;
    }
}
