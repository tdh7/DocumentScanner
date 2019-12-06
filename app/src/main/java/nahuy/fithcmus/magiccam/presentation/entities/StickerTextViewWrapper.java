package nahuy.fithcmus.magiccam.presentation.entities;

import com.xiaopo.flying.sticker.StickerView;

/**
 * Created by huy on 6/2/2017.
 */

public class StickerTextViewWrapper{
    private StickerView stickerView;
    private String text;

    public StickerTextViewWrapper(StickerView stickerView, String text) {
        this.stickerView = stickerView;
        this.text = text;
    }

    public StickerView getStickerView() {
        return stickerView;
    }

    public String getText() {
        return text;
    }
}
