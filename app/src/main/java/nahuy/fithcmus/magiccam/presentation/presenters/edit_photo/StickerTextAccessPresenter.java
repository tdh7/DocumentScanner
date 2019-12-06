package nahuy.fithcmus.magiccam.presentation.presenters.edit_photo;

import android.content.Context;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.use_cases.GettingStickerTextUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerTextAccessPresenter {

    public ArrayList<StickerText> getAllStickers(Context context){
        GettingStickerTextUseCase gettingStickerTextUseCase = new GettingStickerTextUseCase();
        ArrayList<StickerText> stickerTexts = gettingStickerTextUseCase.getAllStickerTexts();
        return stickerTexts;
    }
}
