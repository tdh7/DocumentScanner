package nahuy.fithcmus.magiccam.presentation.entities;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

public class MainNavItem {
    private String name;
    private int resId;
    private Class invokedActivity;

    public MainNavItem(String name, int resId, Class invokedActivity) {
        this.name = name;
        this.resId = resId;
        this.invokedActivity = invokedActivity;
    }

    public void setImageView(final Context context, final ImageView imageView){
        imageView.setImageDrawable(ContextCompat.getDrawable(context, resId));
    }

    public void setTextView(final TextView textView){
        textView.setText(name);
    }

    public Class getInvokedActivity() {
        return invokedActivity;
    }
}