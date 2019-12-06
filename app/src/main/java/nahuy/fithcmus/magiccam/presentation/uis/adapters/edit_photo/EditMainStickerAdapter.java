package nahuy.fithcmus.magiccam.presentation.uis.adapters.edit_photo;

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
import nahuy.fithcmus.magiccam.presentation.commanders.impl.frame.FrameCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.sticker.StickerEditCommander;
import nahuy.fithcmus.magiccam.presentation.entities.Sticker;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.FragEditInteract;

/**
 * Created by huy on 6/2/2017.
 */

public class EditMainStickerAdapter extends RecyclerView.Adapter<EditMainStickerAdapter.EditMainStickerViewHolder> {

    protected Context context;
    private ArrayList<Sticker> lstOfShader;
    private FragEditInteract fragEditInteract;
    int selected_position = 0;


    public static class EditMainStickerViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.filter_image)
        public ImageView filter_main_img;

        private Context context;

        public EditMainStickerViewHolder(Context context, View v){
            super(v);
            ButterKnife.bind(this, v);
            this.context = context;
        }
    }

    public EditMainStickerAdapter(Context context, FragEditInteract fragEditInteract, ArrayList<Sticker> lstOfShader) {

        this.context = context;
        this.fragEditInteract = fragEditInteract;
        this.lstOfShader = lstOfShader;
    }

    @Override
    public EditMainStickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_img, null);

        return new EditMainStickerViewHolder(this.context, v);
    }

    @Override
    public void onBindViewHolder(EditMainStickerViewHolder holder, final int position) {
        lstOfShader.get(position).setStickerDrawableForImageView(context, holder.filter_main_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                fragEditInteract.process(new StickerEditCommander(context, lstOfShader.get(selected_position)));

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstOfShader.size();
    }
}

