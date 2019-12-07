package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import androidx.fragment.app.Fragment;

/**
 * Created by huy on 5/24/2017.
 * Fragment that each navigated bottom fragment must implement for Edit Bottom
 * Fragment know how to change fragment or delegate commander.
 */

public interface BottomNavigateFragmentInterface {
    Fragment getBottomFragment();
    EditTopPresentFragmentCallback getTopFragment();
}
