package org.firstinspires.ftc.teamcode.components;
import android.graphics.Camera;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.components.Gyro;
import org.opencv.core.Scalar;

public class Robot {

    // Robot variables and objects
    private double leftFrontPower = 0;
    private double rightFrontPower = 0;
    private double leftRearPower = 0;
    private double rightRearPower = 0;
    public double speed = 1;
    public double config = 0;

    public CameraManager cameraManager;

    public DcMotor leftFrontDrive;
    public DcMotor rightFrontDrive;
    public DcMotor leftRearDrive;
    public DcMotor rightRearDrive;

    public ElapsedTime elapsedTime;
    public Gyro gyro;
    public Telemetry telemetry;

//    private int encoderZeroPoint;

    // Creates a robot object with methods that we can use in both Auto and TeleOp
    public Robot(HardwareMap hardwareMap, Telemetry telemetry) {

        cameraManager = new CameraManager(hardwareMap);

        // These are the names to use in the phone config (in quotes below)
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftRearDrive = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightRearDrive = hardwareMap.get(DcMotor.class, "rightRearDrive");

        // Defines the forward direction for each of our motors/servos
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.FORWARD);

        // Initializes some other useful tools for our robot (the gyroscope, the timer, etc.)
        gyro = new Gyro(hardwareMap);
        gyro.resetAngle();
        elapsedTime = new ElapsedTime();
        elapsedTime.reset();
        this.telemetry = telemetry;
    }

    public void stopDrive() {
        leftFrontPower = 0;
        rightFrontPower = 0;
        leftRearPower = 0;
        leftRearPower = 0;
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftRearDrive.setPower(leftRearPower);
        rightRearDrive.setPower(rightRearPower);
    }

    // Calculates powers for mecanum wheel drive
    public void calculateDrivePowers(double x, double y, double rotation) {
        y *= -1.0;
        double r = Math.hypot(x, y);
        double robotAngle = Math.atan2(y, x) - Math.PI / 4;
        leftFrontPower = Range.clip(r * Math.cos(robotAngle) + rotation, -1.0, 1.0) * speed;
        rightFrontPower = Range.clip(r * Math.sin(robotAngle) - rotation, -1.0, 1.0) * speed;
        leftRearPower = Range.clip(r * Math.sin(robotAngle) + rotation, -1.0, 1.0) * speed;
        rightRearPower = Range.clip(r * Math.cos(robotAngle) - rotation, -1.0, 1.0) * speed;
    }

    // Sends desired power to drive motors
    public void sendDrivePowers() {
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftRearDrive.setPower(leftRearPower);
        rightRearDrive.setPower(rightRearPower);
    }

    // Updates the powers being sent to the drive motors
    public void updateDrive() {
        //Displays motor powers on the phone
        telemetry.addData("angle", "" + gyro.getAngle());
        telemetry.addData("config: ", "" + config);
        telemetry.update();
        sendDrivePowers();
    }
    // Makes the robot chase the wobble goal (if called repeatedly); TO DO: NEEDS GYRO IMPLEMENTATION
    //TEsting
    // Toggles the drive speed between 50% and normal
    public void toggleSpeed() {
        speed = (speed == 1 ? 0.5 : 1);
    }

    // Resets the timer
    public void resetElapsedTime() {
        elapsedTime.reset();
    }

    // Returns how many seconds have passed since the timer was last reset
    public double getElapsedTimeSeconds() {
        return elapsedTime.seconds();
    }

    // Makes the robot wait (i.e. do nothing) for a specified number of seconds
    public void wait(double seconds) {
        double start = getElapsedTimeSeconds();
        while (getElapsedTimeSeconds() - start < seconds) {}
    }


    // we don't have
//    private double encoderTicksToMeters(int ticks) {
//        double revolutions = (double)ticks / 537.7;
//        double distance = revolutions * 0.30159; // 96mm diameter * pi, converted to meters
//        return distance;
//    }
}