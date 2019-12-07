package nahuy.fithcmus.magiccam.presentation.uis.customs.decorations;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by huy on 2/27/2017.
 */

public class SquareItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    // Square space.
    public SquareItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        // outRect.bottom = space;
        outRect.left = space;
        outRect.right = space;

        if(position == 0){
            outRect.top = space;
        }
        else{
            outRect.top = 0;
        }

    }
}
