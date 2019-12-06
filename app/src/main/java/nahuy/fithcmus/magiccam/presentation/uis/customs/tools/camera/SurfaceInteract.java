package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.graphics.Bitmap;

import java.net.URI;

public interface SurfaceInteract {
	
	public interface CaptureSI extends SurfaceInteract{
		void recvBm(Bitmap bm);
	}
	
	public interface RecordSI extends SurfaceInteract{
		void recvRecord(URI path);
	}
	
}
