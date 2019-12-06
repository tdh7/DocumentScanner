package nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.gallery.GalleryImageCommander;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryFragCallback;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryGridAdapter extends RecyclerView.Adapter<GalleryGridAdapter.GalleryGridViewHolder>{

    // Context.
    private Context mainContext;

    // Array store image fle.
    private List<DA_GalleryImageInfo> myGalleryGridImageItems;

    private int myLayoutId;

    private GalleryClickListener clickListener;

    private boolean setCameraButtonInGrid = false;

    public GalleryGridAdapter(Context mainContext, int resID,
                              List<DA_GalleryImageInfo> images, GalleryClickListener clickListener) {
        this.mainContext = mainContext;
        myGalleryGridImageItems = images;
        myLayoutId = resID;
        this.clickListener = clickListener;
    }

    public void setSetCameraButtonInGrid(boolean setCameraButtonInGrid) {
        this.setCameraButtonInGrid = setCameraButtonInGrid;
    }

    @Override
    public GalleryGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View v = inflater.inflate(myLayoutId, null);

        // Init view holder.
        GalleryGridViewHolder new_vh = new GalleryGridViewHolder(v);
        return new_vh;
    }

    @Override
    public void onBindViewHolder(GalleryGridViewHolder holder, final int position) {

        final int realPosition;

        if(position == 0 && setCameraButtonInGrid){
            holder.imgV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.btn_gallery_camera));
            holder.imgV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentToCamera = new Intent(mainContext, MainActivity.class);
                    mainContext.startActivity(intentToCamera);
                }
            });
            return;
        }

        if(setCameraButtonInGrid){
            realPosition = position - 1;
        }
        else{
            realPosition = position;
        }

        myGalleryGridImageItems.get(realPosition).setImageForImageView(mainContext, holder.imgV);
        holder.imgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GalleryPresenter.getmInstance(mainContext).startEditImage(myGalleryGridImageItems.get(position));
                GalleryFragCallback galleryFragCallback = (GalleryFragCallback)mainContext;
                galleryFragCallback.invokeEdit(new GalleryImageCommander(myGalleryGridImageItems.get(realPosition)));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (setCameraButtonInGrid) {
            return myGalleryGridImageItems.size() + 1;
        }
        else{
            return myGalleryGridImageItems.size();
        }
    }

    public static class GalleryGridViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.gallery_grid_mode_imgv)
        public ImageView imgV;

        public View v;

        public GalleryGridViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
            this.v = v;
        }
    }
}
