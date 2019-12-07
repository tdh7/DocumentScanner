package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;


/**
 * Created by huy on 3/27/2017.
 */

public interface GalleryDisplayStrategy {
    void setUpDisplay(final Context context, RecyclerView component,
                      List<DA_GalleryImageInfo> items, GalleryClickListener clickListener);
}
