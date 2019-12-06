package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.generate_bitmap;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.BitmapUtils;

/**
 * Created by huy on 6/3/2017.
 */

public class GenerateFileChannelBitmap extends GenerateChannelBitmap {
    @Override
    public Bitmap generateBitmap(Context context, String filePath, int w, int h) {
        File channelFolder = new File(context.getFilesDir(), Constants.LOCAL_CHANNEL_NAME);
        String realPath = "";
        if(filePath.contains("data")){
            realPath = filePath;
        }
        else{
            realPath = channelFolder.getAbsolutePath() + "/" + filePath;
        }
        Bitmap rawBitmap = BitmapUtils.decodeRawBitmap(realPath);
        if(w == 0 && h == 0){
            return rawBitmap;
        }
        return this.scale(rawBitmap, w, h);
    }
}
