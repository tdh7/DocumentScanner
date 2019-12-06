package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by huy on 6/1/2017.
 */

public class Frame {
    private String path;

    public Frame(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setImageForImageView(final Context context, final ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }
}
