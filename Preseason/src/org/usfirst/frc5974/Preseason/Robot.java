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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.DoubleSolenoid; 

//don't change the name of this class
public class Robot extends IterativeRobot {
//	ADIS16448_IMU nav;
	Joystick masterRemote;
	Timer Time = new Timer();
	RobotDrive robotdrive;
	Spark rBack = new Spark(3);
	Spark rFront = new Spark(1);
	Spark lBack = new Spark(0);
	Spark lFront = new Spark(2);
	I2C i2c;
	Relay solenoid = new Relay(3);
	Spark clamp = new Spark(4);
	DoubleSolenoid airSolenoid = new DoubleSolenoid(0,1);
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
	boolean turning = false;
	boolean push = false;
	int processStep = 1; //a step of autonomous
	double driveSpeed = .5;
	boolean retracted = false;
	
	public void updateAll(){
		updateController();
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
		// Dpad is silly	
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
	
    Command autonomousCommand;

    public static OI oi;
    	//um ok

    public void robotInit() {
    	//When the robot first turns on
    	RobotMap.init();
    	i2c = new I2C(Port.kOnboard, 8);
		masterRemote = new Joystick(0);
        oi = new OI();
        // instantiate the command used for the autonomous period
        autonomousCommand = new AutonomousCommand();
    }

    public void disabledInit(){
    	//Stuff to do when "disabled" is hit. Don't really need it for now but might as well leave it.
    }

    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    public void autonomousPeriodic() {
    	//Run periodically during auto
    	/* Our Plan
    	 * 1. Go like 40 feet
    	 * 2. Attempt to grab the flag
    	 * 3. Stay there, wait for humans to take over
    	 */
    	
    	//1. go forward, except we're starting out backwards.
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
    
	public void fire(){
		airSolenoid.set(DoubleSolenoid.Value.kForward);
		Timer.delay(0.1);
		airSolenoid.set(DoubleSolenoid.Value.kReverse);
		retracted = false;
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
   
    	//nyoom nyoom
	    	lFront.set((-1*driveSpeed) * (AxisControlLeftY));
			lBack.set((-1*driveSpeed) * (AxisControlLeftY));
			rFront.set(driveSpeed * AxisControlRightY);
			rBack.set(driveSpeed * AxisControlRightY);
    
        //Arduino - stepper motor for ping pong rotator
			
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
				
			//debug I think, can't remember tbh
				if (ButtonX){
					byte[] requestX = {'x'};
					i2c.transaction(requestX, 1, heading, 1);
					SmartDashboard.putNumber("Arduino heading", (double)heading[0]);
				}
    	
		//Clampy boi
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
    	
		//solenoids
			if (BumperRight){
				//solenoid.set(Relay.Value.kReverse);
				//solenoid.set(Relay.Value.kOn);
				fire();
			}
			else if(retracted==false){
				//solenoid.set(Relay.Value.kForward);
				//solenoid.set(Relay.Value.kOff);
				airSolenoid.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(0.1);
				airSolenoid.set(DoubleSolenoid.Value.kOff);
				retracted = true;
			}
		
		//idk, like a thing
        Scheduler.getInstance().run();
    }

    public void testPeriodic() {
    	//just leave it
        LiveWindow.run();
    }}