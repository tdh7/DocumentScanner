package nahuy.fithcmus.magiccam.data.entities.gallery;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by huy on 3/29/2017.
 */

public class DA_GalleryAlbumInfo {
    private long albumId;
    private String albumName;
    private Uri albumImg;
    private int numOfImgs;

    public DA_GalleryAlbumInfo(long albumId, String albumName, Uri albumImg) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumImg = albumImg;
        this.numOfImgs = 1;
    }

    public void setAlbumCoverForImgView(final Context context, final ImageView imgV){
        Glide.with(context)
                .load(albumImg)
                .into(imgV);
    }

    public void setAlbumInfoForTextView(final TextView txtV){
        String infoToSet = String.format("%s (%d)", this.albumName, this.numOfImgs);
        txtV.setText(infoToSet);
    }

    public void setAlbumNameForTextView(final TextView txtV){
        txtV.setText(this.albumName);
    }

    public long getAlbumId(){
        return this.albumId;
    }

    public boolean isThisAlbum(String name){
        return this.albumName.equals(name);
    }

    public void increaseGalleryImage(){
        this.numOfImgs++;
    }
}
