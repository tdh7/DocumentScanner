package nahuy.fithcmus.magiccam.presentation.uis.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGLShader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.SubToolContainerInterface;

/**
 * Created by huy on 6/19/2017.
 */

public class SubToolAdapter extends RecyclerView.Adapter<SubToolAdapter.SubFilterViewHolder> {

    private Context context;
    private SubToolContainerInterface sfi;
    private ArrayList<MyToolGLShader> lstOfShader;
    private int selected_position = -1;

    public SubToolAdapter(Context context, SubToolContainerInterface sfi,
                          ArrayList<MyToolGLShader> lstOfShader) {
        this.context = context;
        this.sfi = sfi;
        this.lstOfShader = lstOfShader;
    }

    public static class SubFilterViewHolder extends RecyclerView.ViewHolder{
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
        if (position == 0) {
            holder.filter_main_img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tool_back_trap));
            // Glide.with(context).load(R.drawable.filter_back).into(holder.filter_main_img);
            // lstOfShader.get(position).setImageViewHeader(context, holder.filter_main_img);
            //holder.bindOnClick(sfi, null);
        } else {
            lstOfShader.get(position - 1).setAssetImageViewHeader(context, holder.filter_main_img);
            //holder.bindOnClick(sfi, lstOfShader.get(position - 1));
        }

        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(Color.BLACK);
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
                if(selected_position == 0){
                    sfi.subCotainerClose();
                }
                else {
                    sfi.subContainerProcess(lstOfShader.get(selected_position - 1));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // One space more for back
        return lstOfShader.size() + 1;
    }
}

