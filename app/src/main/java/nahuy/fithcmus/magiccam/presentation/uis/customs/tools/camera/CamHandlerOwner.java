package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.graphics.SurfaceTexture;

public interface CamHandlerOwner extends SurfaceTexture.OnFrameAvailableListener{

	void setListenerForST();
	void changeRenderOrientation();

}