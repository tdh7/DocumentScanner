package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;


import nahuy.fithcmus.magiccam.presentation.commanders.callbacks.CallingAbstractCommander;
import nahuy.fithcmus.magiccam.presentation.entities.enums.EditTopType;

/**
 * Created by huy on 5/24/2017.
 */

public interface FragEditInteract<T extends Object> {
    void process(CallingAbstractCommander commander);
}
