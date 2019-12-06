package nahuy.fithcmus.magiccam.presentation.presenters.gallery;

import android.content.Context;

import java.util.List;

import nahuy.fithcmus.magiccam.data.clients.local.GalleryDAO;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryAlbumInfo;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryLoadingPublisher;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryImageLoadingPresenter extends GalleryLoadingPublisher {

    public void requestUpdate(final Context context) {
        List<DA_GalleryImageInfo> lstOfImg = GalleryDAO.getInstance().getImages(context);
        this.notifyChange(lstOfImg);
    }

    public void requestUpdateByAlbumName(final Context context, long currentAlbumId) {
        List<DA_GalleryImageInfo> lstOfImg = GalleryDAO.getInstance().getImagesInAlbum(context, currentAlbumId);
        this.notifyChange(lstOfImg);
    }

    public List<DA_GalleryAlbumInfo> getListOfAlbums(final Context context){
        List<DA_GalleryAlbumInfo> albumList = GalleryDAO.getInstance().getAlbumList(context);
        return albumList;
    }

}
