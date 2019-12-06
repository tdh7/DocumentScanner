/**
 * 
 */
package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.graphics.Bitmap;

/**
 * @author huy
 * Surface interact adapter for capture
 */
public class SICaptureAdapter extends SIAdapter {

	public SICaptureAdapter(SurfaceInteract.CaptureSI siIns) {
		super(siIns);
	}

	@Override
	public void recvMediaFile(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof Bitmap){
			((SurfaceInteract.CaptureSI)this.si).recvBm((Bitmap)obj);
		}
	}

}
