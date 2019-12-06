package nahuy.fithcmus.magiccam.presentation.uis.customs.decorations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nahuy.fithcmus.magiccam.R;


/**
 * Created by huy on 2/28/2017.
 */

public class LineDividerDecorator extends RecyclerView.ItemDecoration{

    private int verticalSpacing;
    private Drawable myDividerLine;

    public LineDividerDecorator(Context context, int verticalSpacing){
        myDividerLine = ContextCompat.getDrawable(context, R.drawable.gallery_album_line_divider);
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
            int top = child.getBottom() + verticalSpacing / 2;
            int bottom = top + myDividerLine.getIntrinsicHeight();

            myDividerLine.setBounds(left, top, right, bottom);
            myDividerLine.draw(c);
        }
    }
}
