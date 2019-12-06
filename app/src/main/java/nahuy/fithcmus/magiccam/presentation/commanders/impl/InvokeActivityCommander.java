package nahuy.fithcmus.magiccam.presentation.commanders.impl;

import android.content.Context;
import android.content.Intent;

import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.MainNavItem;

/**
 * Created by huy on 6/2/2017.
 */

public class InvokeActivityCommander implements CallingAbstractCommander {

    private MainNavItem mainNavItem;

    public InvokeActivityCommander(MainNavItem mainNavItem) {
        this.mainNavItem = mainNavItem;
    }

    @Override
    public void process(Object obj) {
        if(obj instanceof Context) {
            if (mainNavItem.getInvokedActivity() != null) {
                Context context = (Context) obj;
                Intent intent = new Intent(context, mainNavItem.getInvokedActivity());
                context.startActivity(intent);
            }
        }
    }
}
