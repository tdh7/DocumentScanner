package nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery;

import android.content.Context;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryDetailAdapter extends RecyclerView.Adapter<GalleryDetailAdapter.GalleryDetailViewHolder>{

    // Context.
    private Context mainContext;

    // Array store image fle.
    private List<List<DA_GalleryImageInfo>> myGalleryListItems;

    private int myLayoutId;

    private GalleryClickListener clickListener;

    public GalleryDetailAdapter(Context mainContext, int myLayoutId,
                                List<List<DA_GalleryImageInfo>> myGalleryListItems,
                                GalleryClickListener clickListener) {
        this.mainContext = mainContext;
        this.myGalleryListItems = myGalleryListItems;
        this.myLayoutId = myLayoutId;
        this.clickListener = clickListener;
    }
    
    @Override
    public GalleryDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View v = inflater.inflate(myLayoutId, null);

        // Init view holder.
        GalleryDetailViewHolder new_vh = new GalleryDetailViewHolder(v);
        return new_vh;
    }

    @Override
    public void onBindViewHolder(GalleryDetailViewHolder holder, int position) {
        myGalleryListItems.get(position).get(0).setCreatedDateInfo(mainContext, holder.lstTitle);
        
        RecyclerView.LayoutManager gridLayout = new GridLayoutManager(mainContext.getApplicationContext(), 4);
        GalleryGridAdapter adapter = new GalleryGridAdapter(mainContext, R.layout.gallery_image_grid_item,
                myGalleryListItems.get(position), clickListener);
        
        holder.lst.setLayoutManager(gridLayout);
        holder.lst.setItemAnimator(new DefaultItemAnimator());
        holder.lst.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return myGalleryListItems.size();
    }

    public static class GalleryDetailViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.gallery_detail_mode_subinfo)
        public TextView lstTitle;

        @BindView(R.id.gallery_detail_mode_sublist)
        public RecyclerView lst;

        public GalleryDetailViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
