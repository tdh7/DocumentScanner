package nahuy.fithcmus.magiccam.presentation.uis.adapters.store;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.uis.adapters.MainNavItemAdapter;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.StoreItemFragmentCalling;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

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
