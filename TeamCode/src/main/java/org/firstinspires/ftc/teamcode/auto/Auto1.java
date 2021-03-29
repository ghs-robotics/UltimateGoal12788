package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.components.CvPipeline;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.openftc.easyopencv.OpenCvPipeline;

//TODO create an auto that can detect the ring stacks and move the wobble goal to the correct dropzone
//this robot will not be able to shoot rings for a few weeks so moving the wobble goal is the
// only thing this robot can do :(
@Autonomous(name="Auto1", group="Linear Opmode")
public class Auto1 extends LinearOpMode {

    Robot robot;
    CvPipeline pipeline;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap, telemetry);
        pipeline = robot.cameraManager.pipeline;

        telemetry.addData("Status","Initialized");
        telemetry.update();

        waitForStart();
        robot.resetElapsedTime();


        while(robot.getElapsedTimeSeconds() < 2.0) {
            robot.calculateDrivePowers(0.0, 0.6, 0.0);
            robot.updateDrive();
        }

        //Detect how many rings there are in the stack

        //Move to the tower goal

        //Turn 90 degrees to the left

        //Move to the correct drop zone

        //Drop off wobble goal

    }

    public void madeIt(String s) {
        robot.telemetry.addData("Made it! Status: ", s);
        robot.telemetry.update();
        robot.wait(0.0);
    }
}
