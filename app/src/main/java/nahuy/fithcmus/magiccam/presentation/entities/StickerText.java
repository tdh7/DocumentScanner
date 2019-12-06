package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;

import nahuy.fithcmus.magiccam.R;

import static android.R.attr.dashGap;
import static android.R.attr.resource;

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