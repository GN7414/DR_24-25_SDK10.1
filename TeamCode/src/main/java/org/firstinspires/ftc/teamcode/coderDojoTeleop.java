package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="coderDojoTeleop")
@Disabled

public class coderDojoTeleop extends LinearOpMode
{
    public boolean buttonB = true;
    public boolean buttonA = true;
    public boolean buttonC = true;
    public boolean openClose = true;

    public Servo armWrist = null;
    public CRServo inTake = null;
    public Servo slideFingerR = null;
    public Servo slideFingerL = null;
    public Servo dumper = null;

    public DcMotor arm = null;
    public DcMotor slides = null;

    public int slideEncoder = 0;
    public double speed = 0.3;

    public int maxSlides = 8500;
    public int downPos = 650;

    public boolean down = false;
    public boolean open = false;


    public enum AutoGrab
    {
        START,
        GRAB_SAMPLE,
        DROP_SAMPLE,
        TOP_SLIDE_POS,
        MIDDLE_SLIDE_POS,
        BOTTOM_SLIDE_POS,
        ARM_TOP_POS,
        ARM_MIDDLE_POS,
        ARM_BOTTOM_POS,
        MANUAL
    }
    AutoGrab autoGrab = AutoGrab.START;

    ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException
    {

        robotHardware robot = new robotHardware(hardwareMap);

        robot.resetDriveEncoders();

        //dive motors
        arm = hardwareMap.dcMotor.get("frontArmMotor");
        slides = hardwareMap.dcMotor.get("slides");


        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slides.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slides.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        armWrist = hardwareMap.servo.get("intakeWrist");
        inTake = hardwareMap.crservo.get("intake");
        //slideFingerL = hardwareMap.servo.get("");
        //slideFingerR = hardwareMap.servo.get("");
        dumper = hardwareMap.servo.get("bucket");

        slides.setDirection(DcMotor.Direction.REVERSE);



        while(!isStarted() && !isStopRequested()){


            //smaller numbers go up higher
            dumper.setPosition(0.6);
            armWrist.setPosition(.5);

            inTake.setPower(0);

            arm.setTargetPosition(100);
            arm.setPower(0.175);
            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        arm.setTargetPosition(100);
        arm.setPower(0.175);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        while (opModeIsActive())
        {


            if(gamepad1.b){

                arm.setTargetPosition(450);
                arm.setPower(0.4);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            if(gamepad1.y){

                arm.setTargetPosition(100);
                arm.setPower(0.4);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            if(gamepad1.dpad_up){
                slides.setTargetPosition(5000);
                slides.setPower(1);
                slides.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }
            if(gamepad1.dpad_down){
                slides.setTargetPosition(0);
                slides.setPower(1);
                slides.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

























            robot.mecanumDrive(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, speed); //normal people
            //robot.mecanumDrive(-gamepad1.right_stick_y, -gamepad1.right_stick_x, -gamepad1.left_stick_x, speed); //nolan

            /*
            //Bucket
            if (gamepad1.a){
                dumper.setPosition(.8);// down position
            }
            if (gamepad1.b){
                dumper.setPosition(.65);
            }
            //Front Claw
            //if (gamepad1.x){
            //    armFingerL.setPosition(0.7);
            //
            //}
            //if (gamepad1.y){
            //    armFingerL.setPosition(0.6);
            //
            //}

            //slide fine addjust
            if (gamepad1.a && buttonA){
                buttonA = false;
                //slideEncoder -= 200;
            }
            else if (gamepad1.y && buttonB){

                buttonB = false;
                inTake.setPower (0);
            }
            if (!gamepad1.a && !buttonA){
                buttonA = true;
            }
            else if (!gamepad1.y && !buttonB){
                buttonB = true;
            }
            if (gamepad1.left_stick_button) {

                downPos = arm.getCurrentPosition() - 5;
                arm.setTargetPosition(downPos);
                arm.setPower(.4);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }

            if (gamepad1.right_stick_button) {

                downPos = arm.getCurrentPosition() + 5;
                arm.setTargetPosition(downPos);
                arm.setPower(.4);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            }



            //One button press
            if (gamepad1.right_trigger > .5 && buttonC){
                buttonC = false;
                if (openClose) {
                    autoGrab = AutoGrab.DROP_SAMPLE;
                }
                else if (!openClose) { //this closes
                    autoGrab = AutoGrab.GRAB_SAMPLE;
                }
                open = false;
            }
            else if (gamepad1.right_trigger < .5 && !buttonC) {
                buttonC = true;
            }


            //Arm
            if (gamepad1.dpad_right){//collect prep
                autoGrab = AutoGrab.ARM_MIDDLE_POS;
            }
            if (gamepad1.dpad_down){//bottom position for arm
                autoGrab = AutoGrab.ARM_BOTTOM_POS;
                down = true;
            }
            if (gamepad1.dpad_up){//back position
                autoGrab = AutoGrab.ARM_TOP_POS;

                down = false;
            }


            if (gamepad1.left_bumper ) {// 8700 is max

                slideEncoder = 8500;

            }
            if (gamepad1.left_trigger > .5 ) {

                slideEncoder = 4250;

            }
            if (gamepad1.right_bumper ) {

                slideEncoder = 0;

            }


            switch (autoGrab)
            {
                case GRAB_SAMPLE:


                    openClose = true;
                    inTake.setPower (.75);

                    break;
                case DROP_SAMPLE:

                    openClose = false;
                    inTake.setPower (-1);

                    break;
                case TOP_SLIDE_POS:
                    slideEncoder = 8500;

                    break;
                case MIDDLE_SLIDE_POS:
                    slideEncoder = 4250;

                    break;
                case BOTTOM_SLIDE_POS:
                    slideEncoder = 0;

                    break;
                case ARM_TOP_POS:
                    down = false;

                    armWrist.setPosition(.6);
                    arm.setTargetPosition(25);
                    arm.setPower(0.25);
                    arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;
                case ARM_MIDDLE_POS:
                    armWrist.setPosition(.5);
                    arm.setTargetPosition(450);
                    arm.setPower(0.25);
                    arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    if (!down){
                        //autoGrab = AutoGrab.DROP_SAMPLE;
                    }

                    break;
                case ARM_BOTTOM_POS:
                    down = true;

                    armWrist.setPosition(.4);
                    arm.setTargetPosition(downPos);
                    arm.setPower(0.5);
                    arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;



            }
























            //Slides stops
            if (slideEncoder < 0){
                slideEncoder = 0;
            }
            if (slideEncoder > maxSlides){
                slideEncoder = maxSlides;
            }
            if (arm.getCurrentPosition() < 100){
                slideEncoder = 0;

            }

            //More slides stuff
            //2200 is the max for the slides
            slides.setTargetPosition(slideEncoder);
            slides.setPower(1);
            slides.setMode(DcMotor.RunMode.RUN_TO_POSITION);

             */

            robot.refresh(robot.odometers);

            telemetry.addData("slide encoder",slideEncoder);
            telemetry.addData("slide pos",slides.getCurrentPosition());

            telemetry.addData("",null);

            telemetry.addData("X",robot.GlobalX);
            telemetry.addData("Y",robot.GlobalY);
            telemetry.addData("Heading",robot.GlobalHeading);


            telemetry.addData("",null);

            telemetry.addData("currentRightPos",robot.odometers[0].getCurrentPosition());
            telemetry.addData("currentLeftPos",robot.odometers[1].getCurrentPosition());
            telemetry.addData("currentPerpendicularPos",robot.odometers[2].getCurrentPosition());

            telemetry.addData("armPos",arm.getCurrentPosition());

            telemetry.update();


        }

    }
}
