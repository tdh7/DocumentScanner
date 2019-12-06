package nahuy.fithcmus.magiccam.presentation.uis.adapters.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryAlbumInfo;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.GalleryClickListener;

/**
 * Created by huy on 3/29/2017.
 */

public class GalleryAlbumAdapter extends RecyclerView.Adapter<GalleryAlbumAdapter.AlbumHolder> {

    private Context mContext;
    private int layoutId;
    private List<DA_GalleryAlbumInfo> lstOfAlbums;
    private GalleryClickListener invokeListener;

    public GalleryAlbumAdapter(Context context, int layoutId, List<DA_GalleryAlbumInfo> lstOfAlbums){
        this.mContext = context;
        this.layoutId = layoutId;
        this.lstOfAlbums = lstOfAlbums;
    }

    public void setClickListener(GalleryClickListener listener){
        this.invokeListener = listener;
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View v = inflater.inflate(this.layoutId, null);

        return new AlbumHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, final int position) {
        lstOfAlbums.get(position).setAlbumInfoForTextView(holder.albumName);
        lstOfAlbums.get(position).setAlbumCoverForImgView(mContext, holder.imgTitle);
        holder.bind(this.invokeListener, lstOfAlbums.get(position));
    }

    @Override
    public int getItemCount() {
        return lstOfAlbums.size();
    }

    public static class AlbumHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.gallery_album_container)
        public LinearLayout albumContainer;

        @BindView(R.id.gallery_album_cover)
        public ImageView imgTitle;

        @BindView(R.id.gallery_album_name_in_list)
        public TextView albumName;

        public AlbumHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final GalleryClickListener invokeListener,final DA_GalleryAlbumInfo albumInfo){
            albumContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeListener.onClick(albumInfo);
                }
            });
        }
    }


}
