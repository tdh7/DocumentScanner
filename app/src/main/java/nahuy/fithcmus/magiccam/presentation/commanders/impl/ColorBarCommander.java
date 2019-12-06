package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.uis.activities.MainActivity;

/**
 * Created by huy on 6/19/2017.
 */

public class ColorBarCommander implements CallingAbstractCommander {
    @Override
    public void process(Object obj) {
        if(obj instanceof MainActivity){
            MainActivity activity = (MainActivity)obj;
            activity.popColorBar();
        }
    }
}

