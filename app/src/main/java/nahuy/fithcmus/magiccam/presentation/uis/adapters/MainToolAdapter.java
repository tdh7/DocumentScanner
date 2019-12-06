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
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.tool_kit.MyToolGlHeader;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.MainContainerInterface;

import static android.R.attr.id;

/**
 * Created by huy on 6/19/2017.
 */

public class MainToolAdapter extends RecyclerView.Adapter<MainToolAdapter.MainToolViewHolder> {

    protected Context context;
    private MainContainerInterface mfi;
    private ArrayList<MyToolGlHeader> lstOfShader;
    int selected_position = -1;

    public MainToolAdapter(Context context, MainContainerInterface mfi, ArrayList<MyToolGlHeader> lstOfShader) {
        this.context = context;
        this.mfi = mfi;
        this.lstOfShader = lstOfShader;
    }

    public static class MainToolViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.filter_image)
        ImageView filter_main_img;

        public MainToolViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public MainToolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_img, null);

        return new MainToolViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainToolViewHolder holder, final int position) {
        lstOfShader.get(position).setImageViewHeader(context, holder.filter_main_img);

        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(Color.WHITE);
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
                    mfi.mainContainerId(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstOfShader.size();
    }
}
