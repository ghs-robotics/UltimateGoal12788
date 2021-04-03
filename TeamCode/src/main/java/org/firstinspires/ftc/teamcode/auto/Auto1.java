package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.components.CvPipeline;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.openftc.easyopencv.OpenCvPipeline;

//TODO create an auto that can detect the ring stacks and move the wobble goal to the correct dropzone
//this robot will not be able to shoot rings for a few weeks so moving the wobble goal is the
// only thing this robot can do :(
@Autonomous
class Auto1 extends LinearOpMode {

    Robot robot;
    CvPipeline pipeline;
    public static final int[] SHOOTING_POSITION = new int[]{170,90};
    public static final int[] CONFIG_0 = new int[]{30, 107};
    public static final int[] CONFIG_1 = new int[]{103, 115}; //TODO: CAN BARELY SEE TOWER GOAL FROM HERE!
    public static final int[] CONFIG_4 = new int[]{100, 80}; // TODO: CAN'T SEE TOWER GOAL FROM THIS POS!
    public static final int[] SECOND_WOBBLE = new int[]{53, 73};
    public static final int[] RING_STACK_FRONT = new int[]{100, 80};
    public static final int[] RING_STACK_BACK = new int[]{100, 70};
    public static final int PARK_W = 95; //Tower width at launch line

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap, telemetry);
        robot.init();
        robot.setTargetTo("tower");
        pipeline = robot.cameraManager.pipeline;

        telemetry.addData("Status","Initialized");
        telemetry.update();

        waitForStart();
        robot.resetElapsedTime();

        //Detect how many rings there are in the stack
        int config = robot.identifyRingConfig();
        madeIt("Config is " + config);

        //Move to the tower goal and shoot
        robot.moveToPos(SHOOTING_POSITION);
        madeIt("Moved to " + SHOOTING_POSITION.toString());
        robot.adjustAndShoot();
        madeIt("Shot three rings");

        //Turn 90 degrees to the left (so that wobble is in front of robot
        robot.rotateToPos((int)robot.gyro.getAngle() + 90);
        madeIt("Adjusted to " + (int)robot.gyro.getAngle() + " degrees");

        //Move to the correct drop zone
        int[] configPos = CONFIG_0; //Default is set to 0 rings
        switch(config) {
            case 0:
                robot.moveToPos(CONFIG_0);
                configPos = CONFIG_0;
                break;
            case 1:
                robot.moveToPos(CONFIG_1);
                configPos = CONFIG_1;
                break;
            case 4:
                robot.moveToPos(CONFIG_4);
                configPos = CONFIG_4;
                break;
        }
        madeIt("Moved to " + configPos.toString());

        //Drop off wobble goal
        //once there, place down the wobble goal
        placeDownWobble();
        madeIt("Set down wobble goal");

        //These are extra credit points, maybe skip over them if we're low on time
        boolean skippedWobble = true;
        if (robot.getElapsedTimeSeconds() < 20) {//If there is time to get second wobble
            skippedWobble = false;
            robot.moveToPos(SECOND_WOBBLE);
            robot.rotateToPos(180, 5);
            madeIt("Lined up with second wobble goal");
            robot.pickUpWobbleGoal(1.0); //Takes in duration of movement
            madeIt("Picked up second wobble goal");
        }

        //Pick up second set of rings
        if (robot.config != 0) { //If there are any more rings
            robot.moveToPos(RING_STACK_FRONT); //Move in front of rings
            robot.moveToPos(RING_STACK_BACK); //Back into rings
            madeIt("Picked up extra rings");
            robot.adjustAndShoot();
            madeIt("Shot extra rings");
        }

        //If there is still time to drop off second wobble, do it
        if (robot.getElapsedTimeSeconds() < 26 && !skippedWobble) {
            robot.moveToPos(configPos);
            placeDownWobble();
            madeIt("Delivered second wobble");
        }

        //Move to park over launch line
        robot.moveToPos(0, PARK_W);
        robot.stopDrive();
        madeIt("Parked over launch line");
    }

    public void placeDownWobble() {
        robot.turnArm();
        robot.wait(0.4);
        robot.toggleGrab();
        robot.wait(0.4);
        robot.turnArm();
        robot.wait(0.1);
        robot.toggleGrab();
    }

    public void madeIt(String s) {
        robot.telemetry.addData("Made it! Status: ", s);
        robot.telemetry.update();
        robot.wait(0.0);
    }
}
