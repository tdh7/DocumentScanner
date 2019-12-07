package nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery.GalleryGridAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryDisplayStrategy;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryGridDisplay implements GalleryDisplayStrategy {

    @Override
    public void setUpDisplay(final Context context, RecyclerView component
            , List<DA_GalleryImageInfo> items, GalleryClickListener clickListener) {

        GalleryGridAdapter gAdapter = new GalleryGridAdapter(context, R.layout.gallery_image_grid_item
                , items, clickListener);
        gAdapter.setSetCameraButtonInGrid(true);
        if (component != null)
            component.setAdapter(gAdapter);

    }
}
