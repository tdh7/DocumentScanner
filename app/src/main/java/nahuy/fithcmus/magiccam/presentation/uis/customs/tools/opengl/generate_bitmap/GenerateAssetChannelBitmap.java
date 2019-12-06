package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.generate_bitmap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import nahuy.fithcmus.magiccam.presentation.Constants;

/**
 * Created by huy on 6/3/2017.
 */

public class GenerateAssetChannelBitmap extends GenerateChannelBitmap {
    @Override
    public Bitmap generateBitmap(Context context, String filePath, int w, int h) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
            Log.e(Constants.LOG_E, e.getMessage());
            return null;
        }

        return this.scale(bitmap, w, h);
    }
}
