package nahuy.fithcmus.magiccam.presentation.presenters.edit_photo;

import android.content.Context;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.use_cases.GettingFrameUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.GettingStickerUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;

/**
 * Created by huy on 6/20/2017.
 */

public class StickerAccessPresenter {

    public ArrayList<Sticker> getAllStickers(Context context) {
        GettingStickerUseCase gettingStickerUseCase = new GettingStickerUseCase();
        ArrayList<Sticker> stickers = gettingStickerUseCase.getAllStickers();
        return stickers;
    }
}
