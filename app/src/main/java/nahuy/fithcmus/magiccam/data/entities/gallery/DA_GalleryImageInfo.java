package nahuy.fithcmus.magiccam.data.entities.gallery;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huy on 6/2/2017.
 */

public class DA_GalleryImageInfo implements Serializable {

    private Uri imgPath;

    private String albumName;

    private long albumId;

    private long createdDate;

    public DA_GalleryImageInfo(Uri imgPath, String albumName, long albumId, long createdDate) {
        this.imgPath = imgPath;
        this.albumName = albumName;
        this.albumId = albumId;
        this.createdDate = createdDate;
    }

    public DA_GalleryImageInfo(Uri imgPath) {
        this.imgPath = imgPath;
        this.albumName = "";
        this.albumId = -1;
        this.createdDate = -1;
    }

    public void setImageForImageView(final Context context, final ImageView imgV){
        Glide.with(context)
                .load(imgPath)
                .into(imgV);
    }

    public void setCreatedDateInfo(final Context context, final TextView textView){
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date realCreatedDate = new Date(createdDate);
        Date toDay = new Date();
        if(realCreatedDate.getDay() == toDay.getDay() &&
                realCreatedDate.getMonth() == toDay.getMonth() &&
                realCreatedDate.getYear() == toDay.getYear()) {
            textView.setText("Recent");
        }
        else {
            textView.setText(dataFormat.format(realCreatedDate));
        }
    }

    public boolean compareCreatedDate(DA_GalleryImageInfo info) {
        Date instanceDate = new Date(createdDate);
        Date targetDate = new Date(info.createdDate);
        if(instanceDate.getYear() != targetDate.getYear() ||
                instanceDate.getMonth() != targetDate.getMonth() ||
                instanceDate.getDay() != targetDate.getDay()){
            return false;
        }
        return true;
    }

    public Uri getImgPath() {
        return imgPath;
    }
}