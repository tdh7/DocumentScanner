package nahuy.fithcmus.magiccam.presentation.commanders.impl.sticker;

import android.content.Context;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.StickerText;
import nahuy.fithcmus.magiccam.presentation.entities.StickerTextViewWrapper;

/**
 * Created by huy on 6/2/2017.
 */

public class StickerTextCommander implements CallingAbstractCommander {

    private Context context;
    private StickerText stickerText;

    public StickerTextCommander(Context context, StickerText stickerText) {
        this.context = context;
        this.stickerText = stickerText;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof StickerTextViewWrapper){
            StickerTextViewWrapper stickerTextViewWrapper = (StickerTextViewWrapper)obj;
            this.stickerText.addStickerToStickerView(context, stickerTextViewWrapper);
        }
    }
}
