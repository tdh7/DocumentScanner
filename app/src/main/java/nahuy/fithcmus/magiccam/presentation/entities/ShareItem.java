package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

/**
 * Created by huy on 2/6/2017.
 */

public class ShareItem {

    private String imgPath;
    private String shareName;
    private String sharePackage;
    private Integer shareResId;

    public ShareItem(Integer shareResId, String shareName, String sharePackage) {
        this.shareResId = shareResId;
        this.shareName = shareName;
        this.sharePackage = sharePackage;
    }

    public ShareItem(String imgPath, String shareName, String sharePackage) {
        this.imgPath = imgPath;
        this.shareName = shareName;
        this.sharePackage = sharePackage;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getSharePackage() {
        return sharePackage;
    }

    public void setImgForImageView(final Context context, final ImageView imgV){
        imgV.setImageDrawable(ContextCompat.getDrawable(context, shareResId));
    }
}
