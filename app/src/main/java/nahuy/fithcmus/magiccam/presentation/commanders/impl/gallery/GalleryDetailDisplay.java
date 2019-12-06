package nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery.GalleryDetailAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryDisplayStrategy;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryDetailDisplay implements GalleryDisplayStrategy {



    @Override
    public void setUpDisplay(final Context context, RecyclerView component,
                             List<DA_GalleryImageInfo> items, GalleryClickListener clickListener) {

        List<List<DA_GalleryImageInfo>> doubleListOfImageByDate = filterSingleListIntoDoubleListByCreatedDate(items);

        GalleryDetailAdapter gAdapter = new GalleryDetailAdapter(context, R.layout.gallery_detail_list_item
                , doubleListOfImageByDate, clickListener);
        if (component != null)
            component.setAdapter(gAdapter);
    }

    private List<List<DA_GalleryImageInfo>> filterSingleListIntoDoubleListByCreatedDate(List<DA_GalleryImageInfo> singleList){

        if(singleList == null){
            return new ArrayList<>();
        }

        if(singleList.size() <= 0){
            return new ArrayList<>();
        }

        List<List<DA_GalleryImageInfo>> doubleList = new ArrayList<>();
        List<DA_GalleryImageInfo> tmpList = new ArrayList<>();
        DA_GalleryImageInfo previousDAGalleryImageInfo = singleList.get(0);

        for(DA_GalleryImageInfo currentInfo : singleList){
            if(!previousDAGalleryImageInfo.compareCreatedDate(currentInfo)) {
                previousDAGalleryImageInfo = currentInfo;
                doubleList.add(tmpList);
                tmpList = new ArrayList<>();
            }

            tmpList.add(currentInfo);
        }

        if(tmpList.size() != 0){
            doubleList.add(tmpList);
        }

        return doubleList;
    }
}
