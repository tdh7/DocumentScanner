/**
 * 
 */
package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.hardware.Camera;
import android.os.AsyncTask;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.CameraUtils;

/**
 * @author huy
 * Thread to open Camera
 */
public class CameraThread extends AsyncTask<Integer, Void, Camera> {
    
	private final static String TAG = CameraThread.class.getName();
	
	private CameraSurfaceHandler handlerIns;
	
	public CameraThread(CameraSurfaceHandler handler){
		this.handlerIns = handler;
	}
	
	@Override
	protected Camera doInBackground(Integer... params) {
        // TODO Auto-generated method stub
        while (this.handlerIns.isBlocking());
        this.handlerIns.block();
        Camera cam = openCamera(params[0], params[1], params[2]);

        return cam;
    }

	@Override
	protected void onPostExecute(Camera result) {
        // TODO Auto-generated method stub
        this.handlerIns.getCameraWrapper().setCamera(result);
        this.handlerIns.unBlock();
        super.onPostExecute(result);
        // Send to handler here.
        // Got the lock
        //this.handlerIns.block();
        //try {
        this.handlerIns.sendMessage(this.handlerIns.obtainMessage(CameraSurfaceHandler.CAM_OPEN));
        //}
        //finally {
        //    this.handlerIns.unBlock();
        //}
    }

	/**
     * Opens a camera, and attempts to establish preview mode at the specified width and height.
     * <p>
     * Sets mCameraPreviewWidth and mCameraPreviewHeight to the actual width/height of the preview.
     */
    private Camera openCamera(int desiredWidth, int desiredHeight, int mode) {

        Camera mCamera = null;

        mCamera = openCameraWithMode(mode);

        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }

        Camera.Parameters parms = mCamera.getParameters();

        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        // leave the frame rate set to default
        mCamera.setParameters(parms);
        mCamera.setDisplayOrientation(90);

        return mCamera;

    }

    private Camera openCameraWithMode(int mode){
        //"which" is just an integer flag
        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        for(int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, camInfo);
            if (mode == 0) {
                if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    return Camera.open(i);
                }
            }
            else if(mode == 1){
                if(camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                    break;
                }
            }
        }

        return Camera.open();
    }
    
}
