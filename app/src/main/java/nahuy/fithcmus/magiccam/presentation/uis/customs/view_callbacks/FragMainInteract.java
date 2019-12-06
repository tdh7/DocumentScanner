package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import nahuy.fithcmus.magiccam.presentation.commanders.impl.CameraCommander;
import nahuy.fithcmus.magiccam.presentation.commanders.impl.GLCommander;

public interface FragMainInteract {
    void interactCamera(CameraCommander camAct);
    void interactRender(GLCommander renderAct);
}