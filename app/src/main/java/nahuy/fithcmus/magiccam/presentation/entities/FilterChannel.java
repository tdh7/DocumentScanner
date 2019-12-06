package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.graphics.Bitmap;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.generate_bitmap.GenerateAssetChannelBitmap;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.generate_bitmap.GenerateFileChannelBitmap;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.opengl.GlUtil;

/**
 * Created by huy on 6/3/2017.
 */

public class FilterChannel {
    private String path;
    private int channelW;
    private int channelH;
    private boolean isUsingAsset;
    private Bitmap currentBitmap;
    private int textureId = -1;

    public FilterChannel(String path, int channelW, int channelH, boolean isUsingAsset) {
        this.path = path;
        this.channelW = channelW;
        this.channelH = channelH;
        this.isUsingAsset = isUsingAsset;
    }

    public Bitmap generateBitmapTexture(Context context, int w, int h){
        if(currentBitmap != null)
            return currentBitmap;

        if(isUsingAsset){
            GenerateAssetChannelBitmap generateAssetChannelBitmap = new GenerateAssetChannelBitmap();
            currentBitmap = generateAssetChannelBitmap.generateBitmap(context, path, w, h);
        }
        else{
            GenerateFileChannelBitmap generateDiskChannelBitmap = new GenerateFileChannelBitmap();
            currentBitmap = generateDiskChannelBitmap.generateBitmap(context, path, w, h);
        }
        return currentBitmap;
    }

    public int getTextureId(Context context, int w, int h){
        if(textureId == -1){
            textureId = GlUtil.loadTexture(context, this, w, h);
        }
        return textureId;
    }

    public String getPath() {
        return path;
    }

    public int getChannelW() {
        return channelW;
    }

    public int getChannelH() {
        return channelH;
    }

    public boolean isUsingAsset() {
        return isUsingAsset;
    }

    public void release(){
        if(currentBitmap != null) {
            currentBitmap.recycle();
            currentBitmap = null;
        }
    }

    public static class FilterChannelBitmapWrapper{
        private Bitmap bm;
        private int texId;

        public FilterChannelBitmapWrapper(Bitmap bm, int texId) {
            this.bm = bm;
            this.texId = texId;
        }

        public Bitmap getBm() {
            return bm;
        }

        public int getTexId() {
            return texId;
        }
    }
}
