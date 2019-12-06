package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.StickerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
