package nahuy.fithcmus.magiccam.presentation.uis.customs.decorations;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huy on 6/20/2017.
 */

public class InvidualSpaceDecoration extends RecyclerView.ItemDecoration {
    private int rightSpace;
    private int leftSpace;
    private int topSpace;
    private int bottomSpace;
    private Drawable mDivider;

    public InvidualSpaceDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace) {
        this.bottomSpace = bottomSpace;
        this.rightSpace = rightSpace;
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if(position == 0){
            outRect.set(leftSpace, topSpace, rightSpace, bottomSpace);
        }
        else{
            outRect.set(0, topSpace, rightSpace, bottomSpace);
        }
    }
}
