package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by huy on 5/20/2017.
 */

public class ToastHelper {

    public static void shortNoti(Context context, String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void longNoti(Context context, String s){
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
