package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import android.content.Context;

/**
 * Created by huy on 5/24/2017.
 */

public interface EditMainContainerInterface {
    // Call when clicking on expandable item_img in navigate fragment
    void mainContainerNavigate(BottomNavigateFragmentInterface bottomNavigateFragmentInterface);
    Context takeContext();
    void onMyBackPressed();
}
