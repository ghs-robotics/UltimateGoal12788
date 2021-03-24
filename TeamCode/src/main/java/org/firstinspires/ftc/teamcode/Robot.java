package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
public class Robot {


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

    // Robot variables and objects
    private double leftFrontPower = 0;
    private double rightFrontPower = 0;
    private double leftRearPower = 0;
    private double rightRearPower = 0;
    private double intakePower = 0;
    public double armAngle = 0.45; // Up position
    public double grabAngle = 0.15; // Closed position
    public double shooterAngle = 0.58; // Back (regular) position
    public double speed = 1;
    public double config = 0;

    HardwareMap hardwareMap; // TODO : get rid of this

    public DcMotor leftFrontDrive;
    public DcMotor rightFrontDrive;
    public DcMotor leftRearDrive;
    public DcMotor rightRearDrive;
    public DcMotor intakeMotor;
    public Servo armServo;
    public Servo grabServo;
    public Servo shooterServo;

    public ElapsedTime elapsedTime;
    public Gyro gyro;
    public Telemetry telemetry;

    // Creates a robot object with methods that we can use in both Auto and TeleOp
    public Robot(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;// TODO : get rid of this

        // These are the names to use in the phone config (in quotes below)
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftRearDrive = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightRearDrive = hardwareMap.get(DcMotor.class, "rightRearDrive");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        armServo = hardwareMap.get(Servo.class, "armServo");
        grabServo = hardwareMap.get(Servo.class, "grabServo");
        shooterServo = hardwareMap.get(Servo.class, "shooterServo");

        // Defines the forward direction for each of our motors/servos
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        armServo.setDirection(Servo.Direction.FORWARD);
        grabServo.setDirection(Servo.Direction.FORWARD);
        shooterServo.setDirection(Servo.Direction.FORWARD);

        // Initializes some other useful tools for our robot (the gyroscope, the timer, etc.)
        gyro = new Gyro(hardwareMap);
        gyro.resetAngle();
        elapsedTime = new ElapsedTime();
        elapsedTime.reset();
        this.telemetry = telemetry;
    }

//    public void swap() {// TODO : get rid of this
//        int cameraMonitorViewId = h.appContext.getResources().getIdentifier(
//                "cameraMonitorViewId",
//                "id", h.appContext.getPackageName());
//        phoneCam.stopStreaming();
//        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(
//                OpenCvInternalCamera.CameraDirection.FRONT, cameraMonitorViewId);
//        phoneCam.setPipeline(pipeline);
//        initCamera();
//        startStreaming();
//    }


    // Switches the object that the robot is trying to detect to a ring
    public void setTargetToRing(int x, int y) {
        currentTargetObject = "ring";
        targetX = x;
        targetY = y;
    }
    // Sets servos to starting positions
    public void resetServos() {
        armServo.setPosition(armAngle);
        grabServo.setPosition(grabAngle);
        shooterServo.setPosition(shooterAngle);
    }

    // Makes the robot stop driving
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
        telemetry.addData("shooterAngle", "" + shooterAngle);
        telemetry.addData("angle", "" + gyro.getAngle());
        telemetry.addData("config: ", "" + config);
        telemetry.update();
        sendDrivePowers();
    }
    // Makes the robot chase the wobble goal (if called repeatedly); TO DO: NEEDS GYRO IMPLEMENTATION




    public void moveToPos(int[] pos, double v) {
        moveToPos(pos, 1.0);
    }
    // Toggles the drive speed between 50% and normal
    public void toggleSpeed() {
        speed = (speed == 1 ? 0.5 : 1);
    }


    // Turns the intake motor on or off
    public void toggleIntake() {
        intakePower = (intakePower == 0 ? 0.6 : 0);
        intakeMotor.setPower(intakePower);
    }

    // Turns the arm
    public void turnArm() {
        // Default angle is 0.5 (which is the up position)
        armAngle = (armAngle == 0.88 ? 0.45 : 0.88);
        armServo.setPosition(armAngle);
    }

    // Toggles the wobble gripper
    public void toggleGrab() {
        // Default angle is 0.15 (which means the gripper is closed)
        grabAngle = (grabAngle == 0.48 ? 0.15 : 0.48);
        grabServo.setPosition(grabAngle);
    }

    // Launches a ring by moving the shooterServo
    public void launchRing() {
        shooterAngle = 0.46; // Forward position
        shooterServo.setPosition(shooterAngle);
        wait(0.5);
        shooterAngle = 0.58; // Back position
        shooterServo.setPosition(shooterAngle);
    }

    // Makes robot move forward and pick up wobble goal
    public void pickUpWobbleGoal(double sec) {
        turnArm();
        toggleGrab();
        calculateDrivePowers(0,-0.4,0);
        sendDrivePowers();
        wait(sec); //Adjust this later
        stopDrive();
        toggleGrab();
        wait(0.6);
        turnArm();
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

//Documentation: https://ftctechnh.github.io/ftc_app/doc/javadoc/index.html