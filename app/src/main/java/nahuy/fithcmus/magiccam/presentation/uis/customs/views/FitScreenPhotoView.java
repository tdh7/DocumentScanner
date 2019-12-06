package nahuy.fithcmus.magiccam.presentation.uis.customs.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by huy on 3/1/2017.
 */

public class FitScreenPhotoView extends PhotoView {

    public FitScreenPhotoView(Context context){
        super(context);
    }

    public FitScreenPhotoView(Context context, AttributeSet attr){
        super(context, attr);
    }

    private int marginLeft;
    private int marginTop;
    private boolean isSetMargin = false;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();

        if(d!=null){
            if(d.getIntrinsicHeight() < d.getIntrinsicWidth()) {
                // ceil not round - avoid thin vertical gaps along the left/right edges
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
                marginTop = (int) (Math.abs((float) MeasureSpec.getSize(heightMeasureSpec) / 2.0 - (float) height / 2.0));
                setTopMargins();
                setMeasuredDimension(width, height);
            }
            else if(d.getIntrinsicHeight() > d.getIntrinsicWidth()){
                int height = MeasureSpec.getSize(heightMeasureSpec);
                int width = (int) Math.ceil((float) height * (float) d.getIntrinsicWidth() / (float) d.getIntrinsicHeight());
                marginLeft = (int) (Math.abs((float) MeasureSpec.getSize(widthMeasureSpec) / 2.0 - (float) width / 2.0));
                setLeftMargins();
                setMeasuredDimension(width, height);
            }
            else{
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
            }
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void setLeftMargins() {
        if(isSetMargin) {
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                int leftMargin = layoutParams.leftMargin + marginLeft;
                int topMargin = layoutParams.topMargin;
                int rightMargin = layoutParams.rightMargin;
                int bottomMargin = layoutParams.bottomMargin;
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                requestLayout();
                isSetMargin = false;
            }
        }
    }

    private void setTopMargins() {
        if(isSetMargin) {
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                int leftMargin = layoutParams.leftMargin;
                int topMargin = layoutParams.topMargin + marginTop;
                int rightMargin = layoutParams.rightMargin;
                int bottomMargin = layoutParams.bottomMargin;
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                requestLayout();
                isSetMargin = false;
            }
        }
    }

    public void setSetMargin(boolean setMargin) {
        isSetMargin = setMargin;
    }
}
