/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Camera-related utility functions.
 */
public class CameraUtils {
    private static final String TAG = CameraUtils.class.getName();

    /**
     * Attempts to find a preview size that matches the provided width and height (which
     * specify the dimensions of the encoded video).  If it fails to find a match it just
     * uses the default preview size for video.
     * <p>
     * TODO: should do a best-fit match, e.g.
     * https://github.com/commonsguy/cwac-camera/blob/master/camera/src/com/commonsware/cwac/camera/CameraUtils.java
     */
    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the preferred
        // size, and has the same aspect ratio.
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            Log.d(TAG, "Camera preferred preview size for video is " +
                    ppsfv.width + "x" + ppsfv.height);
        }

        //for (Camera.Size size : parms.getSupportedPreviewSizes()) {
        //    Log.d(TAG, "supported: " + size.width + "x" + size.height);
        //}

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(TAG, "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
        // else use whatever the default size is
    }

    /**
     * Attempts to find a fixed preview frame rate that matches the desired frame rate.
     * <p>
     * It doesn't seem like there's a great deal of flexibility here.
     * <p>
     * TODO: follow the recipe from http://stackoverflow.com/questions/22639336/#22645327
     *
     * @return The expected frame rate, in thousands of frames per second.
     */
    public static int chooseFixedPreviewFps(Camera.Parameters parms, int desiredThousandFps) {
        List<int[]> supported = parms.getSupportedPreviewFpsRange();

        for (int[] entry : supported) {
            //Log.d(TAG, "entry: " + entry[0] + " - " + entry[1]);
            if ((entry[0] == entry[1]) && (entry[0] == desiredThousandFps)) {
                parms.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }

        int[] tmp = new int[2];
        parms.getPreviewFpsRange(tmp);
        int guess;
        if (tmp[0] == tmp[1]) {
            guess = tmp[0];
        } else {
            guess = tmp[1] / 2;     // shrug
        }

        Log.d(TAG, "Couldn't find match for " + desiredThousandFps + ", using " + guess);
        return guess;
    }

    public static void openFrontCam(Camera cam){
        try{
            cam = releaseCamera(cam);

            Camera.CameraInfo info = new Camera.CameraInfo();

            // Try to find a front-facing camera (e.g. for videoconferencing).
            int numCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cam = Camera.open(i);
                    break;
                }
            }

            if (cam == null) {
                Log.d(TAG, "No front-facing camera found; opening default");
                cam = Camera.open();    // opens first back-facing camera
            }

            if (cam == null) {
                throw new RuntimeException("Unable to open camera");
            }

            cam.setDisplayOrientation(90);

            cam.startPreview();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void openBackCam(Camera cam){
        try{
            cam = releaseCamera(cam);

            cam = Camera.open();

            if (cam == null) {
                throw new RuntimeException("Unable to open camera");
            }

            cam.setDisplayOrientation(90);

            cam.startPreview();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void flashOffCamera(Camera cam){
        cam.stopPreview();
        Camera.Parameters preParameters = cam.getParameters();
        List<String> supportedFlashMode = preParameters.getSupportedFlashModes();
        if(supportedFlashMode != null){
            if(supportedFlashMode.contains(Camera.Parameters.FLASH_MODE_OFF)){
                preParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cam.setParameters(preParameters);
            }
        }
        cam.startPreview();
    }

    public static void flashOnCamera(Camera cam){
        cam.stopPreview();
        Camera.Parameters preParameters = cam.getParameters();
        List<String> supportedFlashMode = preParameters.getSupportedFlashModes();
        if(supportedFlashMode != null){
            if(supportedFlashMode.contains(Camera.Parameters.FLASH_MODE_ON)){
                preParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                cam.setParameters(preParameters);
            }
        }
        cam.startPreview();
    }

    public static void flashAutoCamera(Camera cam){
        cam.stopPreview();
        Camera.Parameters preParameters = cam.getParameters();
        List<String> supportedFlashMode = preParameters.getSupportedFlashModes();
        if(supportedFlashMode != null){
            if(supportedFlashMode.contains(Camera.Parameters.FLASH_MODE_AUTO)){
                preParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                cam.setParameters(preParameters);
            }
        }
        cam.startPreview();
    }

    public static Camera releaseCamera(Camera cam){
        cam.stopPreview();
        cam.release();
        cam = null;
        return cam;
    }

}
