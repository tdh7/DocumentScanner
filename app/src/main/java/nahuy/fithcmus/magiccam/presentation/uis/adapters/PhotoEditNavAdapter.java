package nahuy.fithcmus.magiccam.presentation.uis.adapters;

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
import nahuy.fithcmus.magiccam.presentation.entities.EditNavItem;
import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.EditMainContainerInterface;

/**
 * Created by huy on 5/24/2017.
 */

public class PhotoEditNavAdapter extends RecyclerView.Adapter<PhotoEditNavAdapter.NavViewHolder> {

    private EditMainContainerInterface editMainContainerInterface;
    private ArrayList<EditNavItem> items;
    int selected_position = 0;

    public static class NavViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.nav_image)
        public ImageView imageView;

        @BindView(R.id.nav_text)
        public TextView textView;

        public NavViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public PhotoEditNavAdapter(EditMainContainerInterface editMainContainerInterface, ArrayList<EditNavItem> items){
        this.editMainContainerInterface = editMainContainerInterface;
        this.items = items;
    }

    @Override
    public NavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(editMainContainerInterface.takeContext()).inflate(R.layout.nav_item, null);
        return new NavViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NavViewHolder holder, final int position) {
        this.items.get(position).setImage(editMainContainerInterface.takeContext(), holder.imageView);
        this.items.get(position).setTitle(holder.textView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                editMainContainerInterface.mainContainerNavigate(items.get(selected_position).getFragmentInterface());

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
