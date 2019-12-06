package nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery;

import android.content.Context;
import android.content.Intent;

import nahuy.fithcmus.magiccam.presentation.Constants;
import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.activities.EditPhotoActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.BitmapUtils;

/**
 * Created by huy on 6/3/2017.
 */

public class GalleryImageCommander implements CallingAbstractCommander {

    private DA_GalleryImageInfo imgPath;

    public GalleryImageCommander(DA_GalleryImageInfo imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof Context){
            Context galleryFragCallback = (Context) obj;
            Intent intent = new Intent(galleryFragCallback, EditPhotoActivity.class);
            String realPathFromURI = BitmapUtils.getRealPathFromURI(galleryFragCallback, imgPath.getImgPath());
            intent.putExtra(Constants.EDIT_IMG_PATH_INTENT_KEY, realPathFromURI);
            intent.putExtra(Constants.EDIT_IMG_SHOULD_DELETE, true);
            galleryFragCallback.startActivity(intent);
        }
    }
}
