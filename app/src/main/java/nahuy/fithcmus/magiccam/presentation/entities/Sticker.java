package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.StickerView;

import java.io.Serializable;

/**
 * Created by huy on 3/5/2017.
 */

public class Sticker implements Serializable{

    private String stickerPath;

    public Sticker(String stickerPath) {
        this.stickerPath = stickerPath;
    }

    public void setStickerDrawableForImageView(final Context context, final ImageView imgV){
        Glide.with(context).load(stickerPath).into(imgV);
    }

    public void addStickerToStickerView(final Context context, final StickerView stickerView){
        Drawable d = Drawable.createFromPath(stickerPath);
        stickerView.addSticker(new DrawableSticker(d));
        stickerView.setConstrained(true);
    }
}
