package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.os.Handler;
import android.os.Message;

public class SurfaceInteractHandler extends Handler {

	private SIAdapter sInt;

	private static SurfaceInteractHandler sHandler = new SurfaceInteractHandler();

	public static Handler getInstace(){
		return sHandler;
	}


	
	public final static int RECV_BITMAP = 1001;
	public final static int RECV_VID = 1002;
	
	@Override
	public void handleMessage(Message msg) {
		this.sInt.recvMediaFile(msg.obj);
	}
	
}
