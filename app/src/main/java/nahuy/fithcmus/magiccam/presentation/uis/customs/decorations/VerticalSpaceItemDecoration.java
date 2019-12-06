package nahuy.fithcmus.magiccam.presentation.uis.customs.decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huy on 2/28/2017.
 */

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int posisiton = parent.getChildAdapterPosition(view);
        if(posisiton < (parent.getAdapter().getItemCount() - 1)) {
            outRect.bottom = verticalSpaceHeight;
        }
        else{
            outRect.bottom = 0;
        }
    }
}
