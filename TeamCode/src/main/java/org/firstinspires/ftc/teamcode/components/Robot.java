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
    public String currentTargetObject = "ring";
    public int[] objectVals = new int[]{0,0,0,0}; //X, Y, Width, Height
    public int[] targetVals = new int[]{0,0,0,0}; //X, Y, Width, Height
    public double targetAngle = 0; //Angle of line between the target object and the robot
    //targetAngle is not relative to the robot's orientation!

    public CameraManager cameraManager;

    public DcMotor leftFrontDrive;
    public DcMotor rightFrontDrive;
    public DcMotor leftRearDrive;
    public DcMotor rightRearDrive;

    public ElapsedTime elapsedTime;
    public Gyro gyro;
    public Telemetry telemetry;

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
        sendDrivePowers();
    }

    // Calculates powers for mecanum wheel drive
    public void calculateDrivePowers(double x, double y, double rotation) {
        x *= -1.0;
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

    public void updateObjectValues() {
        int[] val = cameraManager.getObjectData(currentTargetObject);
        for (int i = 0; i < objectVals.length; i++) {
            objectVals[i] = val[i];
        }
    }

    public void setTargetTo(String target) { //Takes "ring", "wobble", or "tower"
        currentTargetObject = target;
        //xPID.resetValues(); yPID.resetValues(); We should add PID later!
        //SET LOWER AND UPPER IN CVPIPELINE
        targetVals[0] = target == "ring" ? 230 : target == "wobble" ? 60 : 140;
        targetVals[1] = target == "ring" ? 190 : target == "wobble" ? 160 : 70;
    }

    public void chaseTarget(String target) {
        double rotation = 0; //Maybe add this as parameter later
        setTargetTo(target);
        updateObjectValues();
        calculateDrivePowers(targetVals[0], targetVals[1], rotation);
        sendDrivePowers();
    }

    public void moveToPos(int[] pos) {
        double maxSeconds = 1.0; //Maybe add this as parameter later
        setTargetTo("tower");
        updateObjectValues();
        double t = getElapsedTimeSeconds();
        while((Math.abs(targetVals[2] - objectVals[2]) > 8 || Math.abs(targetVals[0] - objectVals[0]) > 8)
                && elapsedTime.seconds() - t < 5) {
            chaseTarget("tower");
        }
        t = getElapsedTimeSeconds();
        while ((leftRearPower != 0 || rightRearPower != 0 || leftFrontPower != 0
                || rightFrontPower != 0) && elapsedTime.seconds() - t < maxSeconds) {
            chaseTarget("tower");
        }
        //stopDrive();
    }

    public void rotateToPos(int angle, int maxSeconds) {
        targetAngle = angle;
        double t = getElapsedTimeSeconds();
        while(Math.abs(targetAngle - gyro.getAngle()) > 5 && elapsedTime.seconds() - t < 5) {
            adjustAngle();
        }
        t = getElapsedTimeSeconds();
        while ((Math.abs(targetAngle - gyro.getAngle()) > 1)
                && elapsedTime.seconds() - t < maxSeconds) {
            adjustAngle();
        }
        //stopDrive();
    }

    // Makes the robot line up with the tower goal and shoot three rings
    public void adjustAndShoot() {
        setTargetTo("tower"); //We may not need this since chaseTarget already does this
        targetVals[0] = 95; targetVals[2] = 80; //Set target width and height for tower
        updateObjectValues();
        double t = getElapsedTimeSeconds();
        while(Math.abs(targetVals[2] - objectVals[2]) > 2 || Math.abs(targetVals[0] - objectVals[0]) > 2 && elapsedTime.seconds() - t < 4) {
            chaseTarget("tower");
        }
        t = getElapsedTimeSeconds();
        chaseTarget("tower"); //We may not need this either
        toggleShooter(); //THIS WON'T WORK UNTIL WE HAVE A SHOOTER CLASS
        while ((leftRearPower != 0 || rightRearPower != 0 || leftFrontPower != 0
                || rightFrontPower != 0) && elapsedTime.seconds() - t < 3) {
            chaseTarget("tower");
        }
        stopDrive();
        for (int i = 0; i < 3; i++) {
            launchRing(); //DOESN'T DO ANYTHING YET
            wait(0.4);
        }
        toggleShooter(); //THIS WON'T WORK UNTIL WE HAVE A SHOOTER CLASS
    }

    public void adjustAngle() {
        double pidConst = -0.033; //Maybe adjust this later... Temp because we don't have PID yet
        calculateDrivePowers(0, 0, (targetAngle - gyro.getAngle()) * pidConst);
        sendDrivePowers();
    }

    // Toggles the drive speed between 50% and normal
    public void toggleSpeed() {
        speed = (speed == 1 ? 0.5 : 1);
    }

    // Turns the shooter motor on or off (WE DON'T HAVE A SHOOTER CLASS YET SO THIS WON'T WORK)
    public void toggleShooter() {
        //diffy.toggleShooter(0);
    }

    //We need to figure out how to launch rings without a diffy launcher
    public void launchRing() {
        /*
        shooterAngle = 0.46; // Forward position
        shooterServo.setPosition(shooterAngle);
        wait(0.5);
        shooterAngle = 0.58; // Back position
        shooterServo.setPosition(shooterAngle); */
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
}