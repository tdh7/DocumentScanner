package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import android.graphics.Bitmap;
import androidx.fragment.app.Fragment;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;

/**
 * Created by nahuy on 5/24/2017.
 */

public interface EditTopPresentFragmentCallback {
    Fragment getFragment(Bitmap bm);
    void process(CallingAbstractCommander callingAbstractCommander);
    Bitmap getProduct();
}
