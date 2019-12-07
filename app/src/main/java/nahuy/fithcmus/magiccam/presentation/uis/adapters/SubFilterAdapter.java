package nahuy.fithcmus.magiccam.presentation.uis.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubContainerInterface;

/**
 * Created by huy on 5/22/2017.
 */

public class SubFilterAdapter extends RecyclerView.Adapter<SubFilterAdapter.SubFilterViewHolder> {

    private Context context;
    private SubContainerInterface sfi;
    private ArrayList<MyGLEffectShader> lstOfShader;
    private int color = Color.WHITE;
    int selected_position = 0;

    public SubFilterAdapter(Context context, SubContainerInterface sfi, ArrayList<MyGLEffectShader> lstOfShader) {
        this.context = context;
        this.sfi = sfi;
        this.lstOfShader = lstOfShader;
    }

    public class SubFilterViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mRootLayout;

        @BindView(R.id.filter_image)
        ImageView filter_main_img;

        public SubFilterViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    @Override
    public SubFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_img, null);

        return new SubFilterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SubFilterViewHolder holder, final int position) {
        lstOfShader.get(position).setAssetImageViewHeader(context, holder.filter_main_img);

        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(color);
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.roundedColorOfImage));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                // Do your another stuff for your onClick
                if(lstOfShader.get(selected_position) == null){
                    return;
                }
                else{
                    sfi.subContainerProcess(lstOfShader.get(selected_position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        // One space more for back
        return lstOfShader.size();
    }

    public void setColor(int color) {
        this.color = color;
    }
}
