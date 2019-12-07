package nahuy.fithcmus.magiccam.presentation.uis.adapters.store;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreItemAdapter{

    public static class StoreItemAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.filter_image)
        ImageView filter_main_img;

        public StoreItemAdapterViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
