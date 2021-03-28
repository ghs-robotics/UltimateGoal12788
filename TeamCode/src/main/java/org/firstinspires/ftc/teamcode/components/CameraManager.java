package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

public class CameraManager {

    // CAUTION, I changed the type of the camera to OpenCvCamera so I can make the cameras interchangeable
//    public OpenCvInternalCamera phoneCam;
    public OpenCvCamera phoneCam;
    public  CvPipeline pipeline;
    // Current camera alternates between the phoneCam and the webcam
//    public OpenCvCamera currentCamera;

    public CameraManager(HardwareMap hardwareMap) {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        pipeline = new CvPipeline();
        phoneCam.setPipeline(pipeline);
    }

    // Initialize the camera
    public void initCamera() {
        startStreaming();
        /*
        // Sets the viewport policy to optimized view so the preview doesn't appear 90 deg
        // out when the RC activity is in portrait. We do our actual image processing assuming
        // landscape orientation, though.
        currentCamera.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
        currentCamera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                startStreaming();
            }
        });
        */
    }

    //Start streaming frames on the phone camera
    public void startStreaming() {
//        phoneCam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_RIGHT);
    }

    //Stop streaming frames on the phone camera
    public void stopStreaming() {
        phoneCam.stopStreaming();
    }

    public int[] getObjectData(String target) {
        return pipeline.getObjectData();
    }
}