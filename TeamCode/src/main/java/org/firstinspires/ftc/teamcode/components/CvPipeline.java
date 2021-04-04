package org.firstinspires.ftc.teamcode.components;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class CvPipeline extends OpenCvPipeline {
    public static final Scalar LOWER_RING_HSV = new Scalar(74, 153, 144); // original values: 74, 153, 144
    public static final Scalar UPPER_RING_HSV = new Scalar(112, 242, 255); // original values: 112, 242, 255
    public static final Scalar LOWER_TOWER_HSV = new Scalar(122, 188, 38); // original values: 0, 124, 60
    public static final Scalar UPPER_TOWER_HSV = new Scalar(169, 255, 162); // original values: 54, 212, 255
    public static final Scalar LOWER_WOBBLE_HSV = new Scalar(0, 117, 0);
    public static final Scalar UPPER_WOBBLE_HSV = new Scalar(77, 255, 97);

    // CV detection variables
    public static Scalar lower = LOWER_TOWER_HSV; // We identify rings by default to start out
    public static Scalar upper = UPPER_TOWER_HSV;
    public static double cover = 0; // The fraction of the top part of the camera screen that is
    // covered, which is useful when we don't want the phone to detect anything beyond the field

    public static Scalar RED = new Scalar(255,0,0); // The fraction of the top part of the camera screen that is
    public static Scalar GREEN = new Scalar(0,255,0); // The fraction of the top part of the camera screen that is
    public static Scalar BLUE = new Scalar(0,0,255); // The fraction of the top part of the camera screen that is

    private boolean objectNotIdentified = false; // The program will know when the object isn't in view
    public CameraManager cameraManager;

    private int targetX = 100;
    private int targetY = 140;
    private int targetWidth = 95;
    private int objectX = 0;
    private int objectY = 0;
    private int objectWidth = 0;
    private int objectHeight = 0;
    private double x = 0;
    private double y = 0;
    public double targetAngle = 0; // gyroscope will target this angle

    private String currentTargetObject = "ring";

    public int[] getObjectData() {
        return new int[]{objectX, objectY, objectWidth, objectHeight};
    }

    //supposed to return int array of object coordinates
    public Mat findObjectCoordinates(Mat src) {

        Imgproc.resize(src, src, new Size(320, 240));

        //Mat dst = src;

        // Convert color from RGB to HSV
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);

        // adding a mask to the dst mat
        // filters colors within certain color range
        Core.inRange(src, lower, upper, src);

        // Get the contours of the ring
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Draw contours on the src image
        Imgproc.drawContours(src, contours, -1, GREEN, 2, Imgproc.LINE_8, hierarchy, 2, new Point());

        Rect largest = new Rect();
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (largest.area() < rect.area() /*&& ringTest(rect.width, rect.height) */) {
                largest = rect;
            }
        }

        // Draw largest rect
        Imgproc.rectangle(src, largest, GREEN, 1); // TODO : comment out?

        //update object values
        objectX = largest.x;
        objectY = largest.y;
        objectWidth = largest.width;
        objectHeight = largest.height;

        return src;
    }

    @Override
    public Mat processFrame(Mat input) {
        return findObjectCoordinates(input);
    }
}
