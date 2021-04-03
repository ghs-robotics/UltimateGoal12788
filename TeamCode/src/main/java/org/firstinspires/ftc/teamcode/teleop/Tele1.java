package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.components.Controller;
import org.firstinspires.ftc.teamcode.components.Robot;

@TeleOp(name="Tele1", group="Iterative Opmode")
public class Tele1 extends OpMode
{
    //Declare OpMode members
    Robot robot;
    Controller controller1;
    //Controller controller2;

    Servo gripTilt, gripGrab = null;

    double gripTiltPos = 0.55;
    double gripGrabPos = 0.25;

    //Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        controller1 = new Controller(gamepad1);
        gripTilt = hardwareMap.get(Servo.class, "gripTilt");
        gripGrab = hardwareMap.get(Servo.class, "gripGrab");
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        robot.cameraManager.initCamera();
    }

    //Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {}

    //Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        robot.resetElapsedTime();
//        gripGrabPos = gripGrab.getPosition();
//        gripTiltPos = gripTilt.getPosition();
    }

    //Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {
        //Registers controller input
        controller1.update();

        //Press "x" to toggle speed between 100% and 30%
        if (controller1.x.equals("pressing")) {
            robot.toggleSpeed();
        }

        if (controller1.dpad_up.equals("pressing")) gripTiltPos += 0.05;
        if (controller1.dpad_down.equals("pressing")) gripTiltPos -= 0.05;
        if (controller1.dpad_left.equals("pressing")) gripGrabPos += 0.05;
        if (controller1.dpad_right.equals("pressing")) gripGrabPos -= 0.05;

        gripTilt.setPosition(gripTiltPos);
        gripGrab.setPosition(gripGrabPos);


//        gripGrabPos = gripGrab.getPosition();
//        gripTiltPos = gripTilt.getPosition();

        telemetry.addData("tilt: ", gripTiltPos);
        telemetry.addData("grab: ", gripGrabPos);

        //Mecanum wheel drive
        robot.calculateDrivePowers(
                controller1.left_stick_x,
                controller1.left_stick_y,
                controller1.right_stick_x
        );
        robot.updateDrive();
    }

    //Code to run ONCE after the driver hits STOP
    @Override
    public void stop(){}
}