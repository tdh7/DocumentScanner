package nahuy.fithcmus.magiccam.presentation.uis.customs.decorations;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huy on 5/22/2017.
 */

public class RightHorizontalListDecoration extends RecyclerView.ItemDecoration {

    private int rightSpace;
    private Drawable mDivider;

    public RightHorizontalListDecoration(int rightSpace) {
        this.rightSpace = rightSpace;
    }

    public RightHorizontalListDecoration(int rightSpace, Drawable mDivider) {
        this.rightSpace = rightSpace;
        this.mDivider = mDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if(position == 0){
            outRect.set(rightSpace, rightSpace, rightSpace, rightSpace);
        }
        else{
            outRect.set(0, rightSpace, rightSpace, rightSpace);
        }
    }

}
