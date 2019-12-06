package nahuy.fithcmus.magiccam.presentation.commanders.impl.sticker;

import android.content.Context;

import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.StickerView;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;

/**
 * Created by huy on 6/2/2017.
 */

public class StickerEditCommander implements CallingAbstractCommander {

    private Context context;
    private Sticker sticker;

    public StickerEditCommander(Context context, Sticker sticker){
        this.context = context;
        this.sticker = sticker;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof StickerView){
            StickerView stickerView = (StickerView)obj;
            sticker.addStickerToStickerView(context, stickerView);
        }
    }

}
