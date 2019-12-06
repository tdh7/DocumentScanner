package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import android.util.Log;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;

/**
 * Created by huy on 6/1/2017.
 */

public class EditGetProductCommander implements CallingAbstractCommander {
    @Override
    public void process(Object obj) {
        Log.i(EditGetProductCommander.class.getName(), "Now take product!");
    }
}
