package com.example.hashcache.views;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;


/** A basic Camera preview class */
public class MyCameraActivity extends Activity{
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Handler backgroundHandler;
        cameraDevice = cameraManager.openCamera(cameraDevice.getId(), CameraDevice.StateCallback, backgroundHandler);
        try {
            CaptureRequest.Builder captureRequest = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequest.addTarget(imageReader.getSurface());
            for(String it: cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(it);

                // Checks where the camera is facing
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)  == CameraCharacteristics.LENS_FACING_BACK) {
                    
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

}
