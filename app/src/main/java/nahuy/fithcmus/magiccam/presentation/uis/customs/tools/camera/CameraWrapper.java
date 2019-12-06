package nahuy.fithcmus.magiccam.presentation.uis.customs.tools.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

public class CameraWrapper {
    private Camera camera;
    private SurfaceTexture surfaceTexture;

    public Camera getCamera() {
        return camera;
    }

    public boolean isCameraNull(){
        return camera == null;
    }

    public boolean isSTNull(){
        return surfaceTexture == null;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public void setPreviewTexture(){
        try {
            camera.stopPreview();
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void setFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener ofal){
        surfaceTexture.setOnFrameAvailableListener(ofal);
    }

    public void releaseCamera(){
        if(camera != null){
            camera.stopPreview();
            try {
                camera.setPreviewTexture(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.release();
            camera = null;
        }
    }

    public void releaseSurfaceTexture(){
        if(surfaceTexture != null){
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }

}