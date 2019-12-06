package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.hardware.Camera;
import android.os.AsyncTask;

import nahuy.fithcmus.magiccam.presentation.uis.customs.tools.CameraUtils;

/**
 * Created by huy on 5/21/2017.
 */

public class CameraSwitchThread extends AsyncTask<Integer, Void, Camera> {

    private CameraSurfaceHandler handlerIns;

    public CameraSwitchThread(CameraSurfaceHandler handler){
        this.handlerIns = handler;
    }

    @Override
    protected Camera doInBackground(Integer... params) {
        // TODO Auto-generated method stub
        Camera cam = null;
        // Wait if main camera thread come first
        while (this.handlerIns.isBlocking());
        this.handlerIns.block();
        cam = openCamera(params[0]);
        return cam;
    }

    @Override
    protected void onPostExecute(Camera result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        this.handlerIns.getCameraWrapper().setCamera(result);
        this.handlerIns.unBlock();
        // Send to handler here.
        this.handlerIns.sendMessage(this.handlerIns.obtainMessage(CameraSurfaceHandler.CAM_OPEN));
    }

    private Camera openCamera(int mode) {
        Camera mCamera = null;

        //this.handlerIns.block();
        //try {
        if(handlerIns.getCameraWrapper().getCamera() != null) {
            mCamera = handlerIns.getCameraWrapper().getCamera();

            mCamera = openCameraPreviewWithMode(mCamera, mode);

            mCamera.setDisplayOrientation(90);
        }
        else{
            mCamera = openCamera(720, 1280, mode);
        }
        //}
        //finally {
         //   this.handlerIns.unBlock();
        //}
        return mCamera;

    }

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

    private Camera openCameraPreviewWithMode(Camera mCamera, int mode) {

        // Previous params
        // Camera.Parameters preParams = mCamera.getParameters();

        // Cam already open
        mCamera = CameraUtils.releaseCamera(mCamera);

        //"which" is just an integer flag
        if (Camera.getNumberOfCameras() >= 2) {
            switch (mode) {
                case 0:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    break;
                case 1:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    break;
            }
        } else {
            mCamera = Camera.open();
        }

        // Set previous params
        // mCamera.setParameters(preParams);
        return mCamera;

    }

}
