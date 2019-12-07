package nahuy.fithcmus.magiccam.presentation.uis.adapters.store;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.StoreItemFragmentCalling;

import static android.media.CamcorderProfile.get;

/**
 * Created by huy on 6/24/2017.
 */

public class StoreFilterAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemAdapterViewHolder> {

    protected Context context;
    protected ArrayList<MyGLEffectShader> lstOfItems;
    protected int color = Color.DKGRAY;
    protected int selected_position = 0;
    protected StoreItemFragmentCalling storeItemFragmentCalling;

    public StoreFilterAdapter(Context context, ArrayList<MyGLEffectShader> lstOfItems, StoreItemFragmentCalling storeItemFragmentCalling) {
        this.context = context;
        this.lstOfItems = lstOfItems;
        this.storeItemFragmentCalling = storeItemFragmentCalling;
    }

    @Override
    public void onBindViewHolder(StoreItemAdapter.StoreItemAdapterViewHolder holder, final int position) {
        MyGLEffectShader currentItem = lstOfItems.get(position);
        currentItem.setAssetImageViewHeader(context, holder.filter_main_img);

        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(color);
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                // Do your another stuff for your onClick
                if(lstOfItems.get(selected_position) == null){
                    return;
                }
                else{
                    storeItemFragmentCalling.queryFilter(lstOfItems.get(selected_position).getId());
                }
            }
        });
    }

    @Override
    public StoreItemAdapter.StoreItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_img, null);

        return new StoreItemAdapter.StoreItemAdapterViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return lstOfItems.size();
    }
}
