/**
 * 
 */
package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import java.net.URI;

/**
 * @author huy
 * Surface Interact adapter for record.
 */
public class SIRecordAdapter extends SIAdapter {

	public SIRecordAdapter(SurfaceInteract.RecordSI siIns) {
		super(siIns);
	}

	@Override
	public void recvMediaFile(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof URI){
			((SurfaceInteract.RecordSI)this.si).recvRecord((URI)obj);
		}
	}
}
