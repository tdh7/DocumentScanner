package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;

public class CameraSurfaceHandler extends Handler {

	private final String TAG = CameraSurfaceHandler.class.getName();

    public static final int SURFACE_COMING = 0;
	public static final int CAM_OPEN = 2000;
	public static final int RELEASE = 4000;
	public static final int RELEASE_ST = 4001;
    public static final int BLOCK_THREAD = 1;

    private CamHandlerOwner handlerOwner;
    private CameraWrapper cameraWrapper;

    private int countDown = 0;

	public CameraSurfaceHandler(CamHandlerOwner owner, CameraWrapper mCam){
		this.handlerOwner = owner;
		this.cameraWrapper = mCam;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		int messId = msg.what;
		
		switch(messId) {
            case SURFACE_COMING:
                cameraWrapper.setSurfaceTexture((SurfaceTexture) msg.obj);
                if (!cameraWrapper.isCameraNull()) {
                    setPreviewTex();
                }
                break;
            case CAM_OPEN:
                if (!cameraWrapper.isSTNull()) {
                    setPreviewTex();
                }
                handlerOwner.changeRenderOrientation();
                break;
            case RELEASE:
                cameraWrapper.releaseCamera();
                break;
            case RELEASE_ST:
                cameraWrapper.releaseSurfaceTexture();
                break;
            case BLOCK_THREAD:
                this.block();
                break;
        }
		
	}

	private void setPreviewTex() {
		handlerOwner.setListenerForST();
		cameraWrapper.setPreviewTexture();
		unBlock();
    }

    public void block(){
        if(countDown == 0){
            countDown = 1;
        }
    }

    public void unBlock(){
        if(countDown == 1){
            countDown = 0;
        }
    }

    public boolean isBlocking(){
        return countDown == 1;
    }

    public CameraWrapper getCameraWrapper() {
        return cameraWrapper;
    }
}