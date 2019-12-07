package nahuy.fithcmus.magiccam.presentation.uis.adapters.edit_photo;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.frame.FrameCommander;
import nahuy.fithcmus.magiccam.presentation.entities.Frame;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;

/**
 * Created by huy on 6/1/2017.
 */

public class EditMainFrameAdapter extends RecyclerView.Adapter<EditMainFrameAdapter.EditMainFrameViewHolder> {

    protected Context context;
    private ArrayList<Frame> lstOfShader;
    private FragEditInteract fragEditInteract;
    int selected_position = 0;

    public static class EditMainFrameViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.filter_image)
        public ImageView filter_main_img;

        public EditMainFrameViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public EditMainFrameAdapter(Context context, FragEditInteract fragEditInteract, ArrayList<Frame> lstOfShader) {

        this.context = context;
        this.fragEditInteract = fragEditInteract;
        this.lstOfShader = lstOfShader;
    }

    @Override
    public EditMainFrameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_img, null);

        return new EditMainFrameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EditMainFrameViewHolder holder, final int position) {
        lstOfShader.get(position).setImageForImageView(context, holder.filter_main_img);

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

                fragEditInteract.process(new FrameCommander(lstOfShader.get(selected_position)));

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstOfShader.size();
    }
}
