package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaopo.flying.sticker.TextSticker;

/**
 * Created by huy on 6/2/2017.
 */

public class StickerText {
    private String stickerPath;
    private int textColor = Color.WHITE;

    public StickerText(String stickerPath, String textColor) {
        this.stickerPath = stickerPath;
        this.textColor = Color.parseColor(textColor);
    }

    public void setStickerDrawableForImageView(final Context context, final ImageView imgV) {
        Glide.with(context).load(stickerPath).into(imgV);
    }

    public void addStickerToStickerView(final Context context, final StickerTextViewWrapper stickerTextViewWrappper) {
        final TextSticker textSticker = new TextSticker(context);
        Drawable d = Drawable.createFromPath(stickerPath);
        textSticker.setDrawable(d);
        textSticker.setText(stickerTextViewWrappper.getText());
        textSticker.setTextColor(textColor);
        textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        textSticker.setMaxTextSize(12);
        textSticker.resizeText();
        stickerTextViewWrappper.getStickerView().addSticker(textSticker);
    }
}