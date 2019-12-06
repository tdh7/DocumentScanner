package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.generate_bitmap;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by huy on 6/3/2017.
 */

public abstract class GenerateChannelBitmap {

    protected Bitmap scale(Bitmap bm, int w, int h){
        return Bitmap.createScaledBitmap(bm, w, h, false);
    }

    public abstract Bitmap generateBitmap(Context context, String filePath, int w, int h);

}
