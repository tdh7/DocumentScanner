/**
 * 
 */
package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

/**
 * @author huy
 * Adapter for surface interact with Activity
 * Using by Handler
 */
public abstract class SIAdapter {

	protected SurfaceInteract si;
	
	public SIAdapter(SurfaceInteract siIns){
		this.si = siIns;
	}
	
	public abstract void recvMediaFile(Object obj);
	
}
