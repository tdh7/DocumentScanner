package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks.BottomNavigateFragmentInterface;


/**
 * Created by huy on 5/24/2017.
 */

public class EditNavItem {
    private String title;
    private int resId;
    private BottomNavigateFragmentInterface fragmentInterface;

    public EditNavItem(String title, int resId, BottomNavigateFragmentInterface fragmentInterface) {
        this.title = title;
        this.resId = resId;
        this.fragmentInterface = fragmentInterface;
    }

    public void setTitle(TextView textView){
        textView.setText(title);
    }

    public void setImage(Context context, ImageView imageView){
        imageView.setImageDrawable(ContextCompat.getDrawable(context, resId));
    }

    public BottomNavigateFragmentInterface getFragmentInterface() {
        return fragmentInterface;
    }
}
