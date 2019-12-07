package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by nahuy on 5/24/2017.
 */

public class FragmentHelper {

    public static void setUpInvidualFragment(AppCompatActivity activity, int id, Fragment fragment){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment);
        ft.commit();
    }
    public static void hideFilterFragment(AppCompatActivity activity, Fragment fragment){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    public static void showFilterFragment(AppCompatActivity activity, Fragment fragment){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.show(fragment);
        ft.commit();
    }
}
