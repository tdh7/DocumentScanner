package nahuy.fithcmus.magiccam.presentation.uis.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.InvokeActivityCommander;
import nahuy.fithcmus.magiccam.presentation.entities.MainNavItem;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.MainNavigateCallback;

/**
 * Created by huy on 6/2/2017.
 */

public class MainNavItemAdapter extends RecyclerView.Adapter<MainNavItemAdapter.MainNavItemViewHolder> {

    protected Context context;
    private MainNavigateCallback mnc;
    private ArrayList<MainNavItem> lstOfShader;
    int selected_position = 0;

    public MainNavItemAdapter(Context context, MainNavigateCallback mnc, ArrayList<MainNavItem> lstOfShader) {
        this.context = context;
        this.mnc = mnc;
        this.lstOfShader = lstOfShader;
    }

    public static class MainNavItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.main_nav_image)
        ImageView nav_main_img;

        @BindView(R.id.main_nav_text)
        TextView nav_main_text;

        public MainNavItemViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public MainNavItemAdapter.MainNavItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_nav_item, null);

        return new MainNavItemAdapter.MainNavItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainNavItemAdapter.MainNavItemViewHolder holder, final int position) {
        lstOfShader.get(position).setImageView(context, holder.nav_main_img);
        lstOfShader.get(position).setTextView(holder.nav_main_text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                mnc.processNavRequest(new InvokeActivityCommander(lstOfShader.get(selected_position)));

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstOfShader.size();
    }
}
