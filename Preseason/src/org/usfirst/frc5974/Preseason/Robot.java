//5974 code for the 2017 preseason game, Capture the Flag
package org.usfirst.frc5974.Preseason;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.usfirst.frc5974.Preseason.commands.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import com.analog.adis16448.frc.ADIS16448_IMU; //nav, but apparently it's garbage
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Solenoid; 

//don't change the name of this class
public class Robot extends IterativeRobot {
//	ADIS16448_IMU nav;
	Joystick masterRemote;
	Timer Time = new Timer();
	CameraServer camera;
	RobotDrive robotdrive;
	Spark rBack = new Spark(3);
	Spark rFront = new Spark(1);
	Spark lBack = new Spark(0);
	Spark lFront = new Spark(2);
	I2C i2c;
	AnalogGyro gyro = new AnalogGyro(1);
	Relay solenoid = new Relay(3);
	Spark clamp = new Spark(4);
	byte[] receiveData = new byte[1];
	
	// Stolen code from last year, reformatting if possible?
	double AxisControlLeftX = 0;
	double AxisControlLeftY = 0;
	double AxisControlRightX = 0;
	double AxisControlRightY = 0;
	boolean fast = false;
	double TriggerLeft;
	double TriggerRight;
	byte triggerLeft = 0;
	byte triggerRight = 0;
	boolean ButtonA;
	boolean APressed;
	boolean ButtonB;
	boolean ButtonX;
	boolean ButtonY;
	boolean ButtonStart;
	boolean ButtonBack;
	boolean BumperLeft;
	boolean BumperRight;
	boolean JoyButtonLeft;
	boolean JoyButtonRight;
	boolean dPadd;
	boolean dPadu;
	boolean dPadl;
	boolean dPadr;
	byte[] heading = {0};
	//byte[] ardX = {0};
	//byte[] ardY = {0};
	//The IMU/10 degrees of freedom
	/*double AngleX;
	double AngleY;
	double AngleZ;
	double HeadingX;
	double HeadingY;
	double HeadingZ;
	double RateX;
	double RateY;
	double RateZ;
	double Pitch;
	double Yaw;
	double Roll;
	double AccelX;
	double AccelY;
	double AccelZ;*/
	boolean turning = false;
	boolean push = false;
	
	//double distZ; *Distance measurement, meant to be used without tele-op.*
	

	int processStep = 1; //a step of a program
	boolean toggleDriveMode = true; 
	boolean toggleAscenderMode = true;
	double driveSpeed = .5;
	double velX = 0;
	double velZ = 0;
	double velY = 0;
	double distX;
	double distY;
	double startPosition;
	boolean XPressed = false;
	boolean JoyRightToggle = false;
	
	public void updateAll(){
		updateController();
		//updateSensors();
	}
	
	public void updateController(){
		updateAxis();
		updateTrigger();
		updateButton();
		updateBumper();
		updateJoy();
		deadZones();
	}
	
	public void deadZones() { //The axes are too accurate and thus need to be cut off
		if(Math.abs(AxisControlLeftY) <= 0.15) {
			AxisControlLeftY = 0;
		}
		if (Math.abs(AxisControlRightY) <= 0.15) {
			AxisControlRightY = 0;
		}
		if(Math.abs(AxisControlLeftX) <= 0.15) {
			AxisControlLeftX = 0;
		}
		if(Math.abs(AxisControlRightX) <= 0.15) {
			AxisControlRightX = 0;
		}
		if(Math.abs(TriggerLeft) <= 0.15) {
			TriggerLeft = 0;
		}
		if(Math.abs(TriggerRight) <= 0.15) {
			TriggerRight = 0;
		}
	}
	
	public void updateTrigger(){ //Updates the Axis on the triggers
		TriggerLeft = masterRemote.getRawAxis(2);
		TriggerRight = masterRemote.getRawAxis(3);
		
		triggerLeft = (byte) (TriggerLeft*255);
		triggerRight = (byte) (TriggerRight*255);
	}
	
	public void updateButton(){ //Updates button values
		ButtonA = masterRemote.getRawButton(1);
		ButtonB = masterRemote.getRawButton(2);
		ButtonX = masterRemote.getRawButton(3);
		ButtonY = masterRemote.getRawButton(4);
		ButtonStart = masterRemote.getRawButton(8);
		ButtonBack = masterRemote.getRawButton(7);
		
		/*
		 * dPadd = masterRemote.getRawButton(13);
		 * dPadu = masterRemote.getRawButton(12);
		 * dPadl = masterRemote.getRawButton(14);
		 * dPadr = masterRemote.getRawButton(15);
		 * 
		 * dPad doesn't seem to like "RawButton". Commented out until we need it. -JavaGreenhorn
		 * dPadl(eft) was meant to towggle fastmode, other buttons have no planned purpose (yet).
		 */	
		
	}
	
	public void updateBumper(){ //Updates the Bumper values
		BumperLeft = masterRemote.getRawButton(5);
		BumperRight = masterRemote.getRawButton(6);
	}
	
	public void updateJoy(){ //Updates the joystick buttons
		JoyButtonLeft = masterRemote.getRawButton(9);
		JoyButtonRight = masterRemote.getRawButton(10);
	}
	
	public void updateAxis(){ //Updates the axes on the joysticks
		AxisControlLeftY = masterRemote.getRawAxis(1);
		AxisControlRightY = masterRemote.getRawAxis(5);
		AxisControlLeftX = masterRemote.getRawAxis(0);
		AxisControlRightX = masterRemote.getRawAxis(4);
	}
	
/*	public void updateAccel(){ //Updates the values for Acceleration
		AccelX = nav.getAccelX();
		AccelY = nav.getAccelY();
		AccelZ = nav.getAccelZ();
		SmartDashboard.putNumber("Accel X", AccelX);
		SmartDashboard.putNumber("Accel Y", AccelY);
		SmartDashboard.putNumber("Accel Z", AccelZ);
	}
	
	public void updateRate(){ //Updates the rate at which one of these values are moving
		RateX = nav.getRateX();
		RateY = nav.getRateY();
		RateZ = nav.getRateZ();
		SmartDashboard.putNumber("Rate X", RateX);
		SmartDashboard.putNumber("Rate Y", RateY);
		SmartDashboard.putNumber("Rate Z", RateZ);
	}
	
	public void updateRotations(){ //Updates the Yaw, Pitch, and Roll values
		Pitch = Math.floor(nav.getPitch());
		Yaw = Math.floor(nav.getYaw());
		Roll = Math.floor(nav.getRoll());
		SmartDashboard.putNumber("Pitch", Pitch);
		SmartDashboard.putNumber("Yaw", Yaw);
		SmartDashboard.putNumber("Roll", Roll);
	}
	
	public void updateAngles(){ //Gets the angle of the gyroscope
		AngleX = nav.getAngleX();
		AngleY = nav.getAngleY();
		AngleZ = nav.getAngleZ();
		angle = nav.getAngle();
		SmartDashboard.putNumber("Angle X", AngleX);
		SmartDashboard.putNumber("Angle Y", AngleY);
		SmartDashboard.putNumber("Angle Z", AngleZ);
		SmartDashboard.putNumber("Angle", angle);
	}
	
	public void updateHeading(){ //Updates the "How far away are we from magnetic north?" values
		HeadingX = nav.getMagX();
		HeadingY = nav.getMagY();
		HeadingZ = nav.getMagZ();
		SmartDashboard.putNumber("Heading X", HeadingX);
		SmartDashboard.putNumber("Heading Y", HeadingY);
		SmartDashboard.putNumber("Heading Z", HeadingZ);
	}
	
	public void updateSensors(){ //Updates the sensor values
		updateHeading();
		updateAccel();
		updateRate();
		updateRotations();
		updateAngles();
	}*/
	
	
    Command autonomousCommand;

    public static OI oi;
    //to be completely honest i have no idea what this is
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//nav = new ADIS16448_IMU();
    	RobotMap.init();
    	i2c = new I2C(Port.kOnboard, 8);
        // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
		masterRemote = new Joystick(0);
        oi = new OI();
        // instantiate the command used for the autonomous period
        autonomousCommand = new AutonomousCommand();
        gyro.calibrate();
    //    nav.calibrate();
    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    public void disabledInit(){

    }

    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	/* 1. Go like 40 feet
    	 * 2. Attempt to grab the flag
    	 * 3. Stay there, wait for humans to take over
    	 */
    	//go forward, except we're starting out backwards.
    	//so go backwards
    	if(processStep == 1){
    		rBack.set(-0.5);
    		rFront.set(-0.5);
    		lBack.set(0.5);
    		lFront.set(0.5);
    		Timer.delay(8);
    		rBack.set(0);
    		rFront.set(0);
    		lBack.set(0);
    		lFront.set(0);
    		processStep += 1;
    	}
    	//Attempt to grab the flag
    	if(processStep == 2){
    		clamp.set(-0.5); //open the claw
    		Timer.delay(0.5);
    		clamp.set(0);
    		clamp.set(0.5); //close the claw
    		Timer.delay(0.5);
    		clamp.set(0);
    		processStep += 1;
    	}
    	
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        updateAll();
        
        masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0.5);
    	masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0.5);
    	Timer.delay(1);
		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0);
		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0);
    }
    
    public void teleopPeriodic() {
    	
    	updateAll();
    	LiveWindow.run();
    	
    	//Drive speed switcher
    	if (ButtonB && !fast){
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 1);
        	masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 1);
        	Timer.delay(0.25);
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0);
    		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0);
    		fast = true;
    	}

    	else if (ButtonB && fast){
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 1);
        	masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 1);
        	Timer.delay(0.25);
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0);
    		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0);
    		fast = false;
    	}
    	
    	if(fast){
    		driveSpeed = 1;
    	}


    	else if(fast == false){
    		driveSpeed = 0.5;
    	}
    	
    	boolean qTurn = false;
    	if (qTurn == false) {
    	lFront.set((-1*driveSpeed) * (AxisControlLeftY));
		lBack.set((-1*driveSpeed) * (AxisControlLeftY));
		rFront.set(driveSpeed * AxisControlRightY);
		rBack.set(driveSpeed * AxisControlRightY);
    	}
		
         
    	//Quick turning
    	//if bumper left turn left
    	if(BumperLeft) {
    		//lFront.set(1);
    		//lBack.set(1);
    		rFront.set(1);
    		rBack.set(1);
    	}
    	//if bumper right turn right
    	if(BumperRight) {
    		//lFront.set(-1);
    		//lBack.set(1);
    		rFront.set(1);
    		rBack.set(1);
    	}
    	//drive code
    	//if turning don't drive
	    	lFront.set((-1*driveSpeed) * (AxisControlLeftY));
			lBack.set((-1*driveSpeed) * (AxisControlLeftY));
			rFront.set(driveSpeed * AxisControlRightY);
			rBack.set(driveSpeed * AxisControlRightY);
    	
        //it's arduino time 
    	//Giving it a speed for the stepper motor and asking it for stuff back

    	if (TriggerLeft>0) {
    		byte[] triggerSend = {'L',triggerLeft};
    		i2c.transaction(triggerSend, 2, receiveData, 1);
    		System.out.println(receiveData[0]);
    	}
    	if (TriggerRight>0) {
    		byte[] triggerSend = {'R',triggerRight};
    		i2c.transaction(triggerSend, 2, receiveData, 1);
    		System.out.println(receiveData[0]);
    	}
    	
    	else if (TriggerRight==0 && TriggerLeft==0){
    		byte[] triggerSend = {0};
    		i2c.transaction(triggerSend, 1, null, 0);
    	}
    	
    	if (ButtonX){
	    	byte[] requestX = {'x'};
	    	i2c.transaction(requestX, 1, heading, 1);
	    	SmartDashboard.putNumber("Arduino heading", (double)heading[0]);
    	}
    	
    	if (ButtonStart){
    		clamp.set(0.5);
    		Timer.delay(0.5);
    		clamp.set(0);
    	}
    	
    	if (ButtonBack){
    		clamp.set(-0.5);
    		Timer.delay(0.5);
    		clamp.set(0);
    	}
    	
    	if (ButtonY){
    		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0.5);
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0.5);
    		Timer.delay(0.25);
    		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0);
    		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0);
    		solenoid.set(Relay.Value.kReverse);
    		solenoid.set(Relay.Value.kOn);

    		
    	}
    	else{
    		solenoid.set(Relay.Value.kForward);
    		solenoid.set(Relay.Value.kOff);
    	}
    	
        //if (ButtonX){//gyro proof of concept
        	/*while(angle < angle+90.0){
        		lFront.set(-0.5);
        		lBack.set(-0.5);
        		rFront.set(-0.5);
        		rBack.set(-0.5);
        		if(angle>=90.0){
        			lFront.set(0);
        			lBack.set(0);
        			rFront.set(0);
        			rBack.set(0);
        			break;
        		}
        	}*/
//        	System.out.println("Fancy gyro says:"+nav.getAngle()+"\n");
        //}

        
        Scheduler.getInstance().run();

    }
    

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    	
    }}