/*
 * 2017 STEAMWORKS ROBOT CODE
 */
package org.usfirst.frc5974.Preseason;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.analog.adis16448.frc.ADIS16448_IMU; //navigation
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;


public class LastYear extends IterativeRobot {
	CameraServer camera; //The Camera
	ADIS16448_IMU Navigation; //10 Degrees of Freedom
	RobotDrive elRobot; //El Roboto
	Joystick masterRemote; //The Joy in my Stick
	Spark motorRight = new Spark(2); //The Drive Motors
	Spark motorLeft  = new Spark(0);
	Spark motorOtherRight = new Spark(3);
	Spark motorOtherLeft = new Spark(1);
	//UpdateThings update = new UpdateThings();
	Timer Time = new Timer(); //I mean it's a timer I guess
	Spark pinch = new Spark(4); //The claw of the claw
	Spark arm = new Spark(5); //The arm of the Claw
	Spark winch = new Spark(6); //The ascending mechanism
	DigitalInput armStopper1 = new DigitalInput(0);
	DigitalInput armStopper2 = new DigitalInput(1);
	//xBox mapping of controllers
	double AxisControlLeftX;
	double AxisControlLeftY;
	double AxisControlRightX;
	double AxisControlRightY;
	double TriggerLeft;
	double TriggerRight;
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
	//The IMU/10 degrees of freedom
	double HeadingX;
	double HeadingY;
	double HeadingZ;
	double AccelX;
	double AccelY;
	double AccelZ;
	double RateX;
	double RateY;
	double RateZ;
	double Altitude;
	double Pitch;
	double Yaw;
	double Roll;
	double Angle;
	double AngleX;
	double AngleY;
	double AngleZ;
	double distZ;
	double Temp;

	int processStep; //a step of a program
	boolean toggleDriveMode = true; 
	boolean toggleAscenderMode = true;
	boolean fastMode = false;
	double driveSpeed;
	double velX = 0;
	double velZ = 0;
	double velY = 0;
	double distX;
	double distY;
	double startPosition;
	boolean XPressed = false;
	boolean JoyRightToggle = false;
	
	
	
	public void updateThings() { //The next few things all have to do with updating values
		updateController();
		updateIMU();
		SmartDashboard.putBoolean("Upper Limit Switch", armStopper1.get());
		SmartDashboard.putBoolean("Lower Limit Switch", armStopper2.get());
	}
	
	public void updateController(){
		//Got button values from control panel, mostly guessed on axes from RoboPong code
		updateAxis();
		updateTrigger();
		updateButton();
		updateBumper();
		updateJoy();
		deadZones();
	}
	
	public void updateAxis(){ //Updates the Axis on the joysticks
		AxisControlLeftY = masterRemote.getRawAxis(1);
		AxisControlRightY = masterRemote.getRawAxis(5);
		AxisControlLeftX = masterRemote.getRawAxis(0);
		AxisControlRightX = masterRemote.getRawAxis(4);
	}
	
	public void updateTrigger(){ //Updates the Axis on the triggers
		TriggerLeft = masterRemote.getRawAxis(2);
		TriggerRight = masterRemote.getRawAxis(3);
	}
	
	public void updateButton(){ //Updates button values
		ButtonA = masterRemote.getRawButton(1);
		ButtonB = masterRemote.getRawButton(2);
		ButtonX = masterRemote.getRawButton(3);
		ButtonY = masterRemote.getRawButton(4);
		ButtonStart = masterRemote.getRawButton(8);
		ButtonBack = masterRemote.getRawButton(7);
	}
	
	public void updateBumper(){ //Updates the Bumper values
		BumperLeft = masterRemote.getRawButton(5);
		BumperRight = masterRemote.getRawButton(6);
	}
	
	public void updateJoy(){ //Updates the joystick buttons
		JoyButtonLeft = masterRemote.getRawButton(9);
		JoyButtonRight = masterRemote.getRawButton(10);
	}
	public void updateIMU(){ //Updates the IMU values
		//IMU time
		updateHeading();
		updateAccel();
		updateRate();
		updateRotations();
		updateAngles();
		updateWeather();
	}
	
	public void updateHeading(){ //Updates the "How far away are we from magnetic north?" values
		HeadingX = Navigation.getMagX();
		HeadingY = Navigation.getMagY();
		HeadingZ = Navigation.getMagZ();
		SmartDashboard.putNumber("Heading X", HeadingX);
		SmartDashboard.putNumber("Heading Y", HeadingY);
		SmartDashboard.putNumber("Heading Z", HeadingZ);
	}
	
	public void updateAccel(){ //Updates the values for Acceleration
		AccelX = Navigation.getAccelX();
		AccelY = Navigation.getAccelY();
		AccelZ = Navigation.getAccelZ();
		SmartDashboard.putNumber("Accel X", AccelX);
		SmartDashboard.putNumber("Accel Y", AccelY);
		SmartDashboard.putNumber("Accel Z", AccelZ);
	}
	
	public void updateRate(){ //Updates the rate at which one of these values are moving
		RateX = Navigation.getRateX();
		RateY = Navigation.getRateY();
		RateZ = Navigation.getRateZ();
		SmartDashboard.putNumber("Rate X", RateX);
		SmartDashboard.putNumber("Rate Y", RateY);
		SmartDashboard.putNumber("Rate Z", RateZ);
	}
	
	public void updateRotations(){ //Updates the Yaw, Pitch, and Roll values
		Pitch = Math.floor(Navigation.getPitch());
		Yaw = Math.floor(Navigation.getYaw());
		Roll = Math.floor(Navigation.getRoll());
		SmartDashboard.putNumber("Pitch", Pitch);
		SmartDashboard.putNumber("Yaw", Yaw);
		SmartDashboard.putNumber("Roll", Roll);
	}
	
	public void updateAngles(){ //Gets the angle of the gyroscope
		AngleX = Navigation.getAngleX();
		AngleY = Navigation.getAngleY();
		AngleZ = Navigation.getAngleZ();
		SmartDashboard.putNumber("Angle X", AngleX);
		SmartDashboard.putNumber("Angle Y", AngleY);
		SmartDashboard.putNumber("Angle Z", AngleZ);
	}
	
	public void updateWeather(){ //Update the Weather
		Temp = Navigation.getTemperature();
		//roborio gets pretty hot, so this number is wrong
		Altitude = Navigation.getBarometricPressure();
		SmartDashboard.putNumber("Temperature", Temp);
		SmartDashboard.putNumber("Altitude", Altitude);
	}
	
	
	
	public void deadZones() { //The Axis are too accurate and thus need to be cut off
		if(Math.abs(AxisControlLeftY) <= 0.1) {
			AxisControlLeftY = 0;
		}
		if (Math.abs(AxisControlRightY) <= 0.1) {
			AxisControlRightY = 0;
		}
		if(Math.abs(AxisControlLeftX) <= 0.1) {
			AxisControlLeftX = 0;
		}
		if(Math.abs(AxisControlRightX) <= 0.1) {
			AxisControlRightX = 0;
		}
		if(Math.abs(TriggerLeft) <= 0.1) {
			TriggerLeft = 0;
		}
		if(Math.abs(TriggerRight) <= 0.1) {
			TriggerRight = 0;
		}
	}
	
	
	/*
	    :::::::::  :::::::: :::::::::  :::::::::::::::::::  :::::::::::::::    :::::::::::::::::::::::::
		:+:    :+::+:    :+::+:    :+::+:    :+:   :+:          :+:    :+:+:   :+:    :+:        :+:    
		+:+    +:++:+    +:++:+    +:++:+    +:+   +:+          +:+    :+:+:+  +:+    +:+        +:+    
		+#++:++#: +#+    +:++#++:++#+ +#+    +:+   +#+          +#+    +#+ +:+ +#+    +#+        +#+    
		+#+    +#++#+    +#++#+    +#++#+    +#+   +#+          +#+    +#+  +#+#+#    +#+        +#+    
		#+#    #+##+#    #+##+#    #+##+#    #+#   #+#          #+#    #+#   #+#+#    #+#        #+#    
		###    ### ######## #########  ########    ###      ##############    ###############    ###    
	 */
	
	public void robotInit() {
		Navigation = new ADIS16448_IMU();
		Navigation.calibrate();
		masterRemote = new Joystick(0);
		updateThings();
		//CameraServer.getInstance().startAutomaticCapture(0);
	}

	/*
	    **                                  *                                                        
     *****                          *     **                                                         
    *  ***                         **     **                                                         
       ***                         **     **                                                         
      *  **       **   ****      ******** **           ****                                   ****   
      *  **        **    ***  * ********  **  ***     * ***  * *** **** ****       ****      * **** *
     *    **       **     ****     **     ** * ***   *   ****   *** **** ***  *   * ***  *  **  **** 
     *    **       **      **      **     ***   *** **    **     **  **** ****   *   ****  ****      
    *      **      **      **      **     **     ** **    **     **   **   **   **    **     ***     
    *********      **      **      **     **     ** **    **     **   **   **   **    **       ***   
   *        **     **      **      **     **     ** **    **     **   **   **   **    **         *** 
   *        **     **      **      **     **     ** **    **     **   **   **   **    **    ****  ** 
  *****      **     ******* **     **     **     **  ******      **   **   **   **    **   * **** *  
 *   ****    ** *    *****   **     **    **     **   ****       ***  ***  ***   ***** **     ****   
*     **      **                           **    **               ***  ***  ***   ***   **           
*                                                *                                                   
 **                                             *                                                    
                                               *                                                     
                                              *                                                      
	 */
	
	public void autonomousInit() {
		/* How to autonomous:
		 * 1. goto 2
		 * 2. Turn towards tower
		 * 3. Drive to our tower
		 * 		7 ft. 9(1/4) in. from alliance wall diamond plate
		 *		    Starting line is 2'6" behind alliance wall diamond plate
		 *				10'3.25" to tower
		 *					Peg is 10.5" from tower
		 *						Final distance: ~ 9'5"
		 * 4. Target peg and drive there
		 * 5. Put gear on peg
		 * 6. Back up, drive to gear loading area
		 * 		Where do we get the gears? Other side of the field?
		 * 7. Pick up gear from ground
		 * 8. goto 3; if autonomous==False: break
		 */
		//Time.start();
		//Navigation.calibrate();
		processStep = 1;
		AngleZ = 0;
		//HeadingZ = startPosition;
	}
	
	public void zeroMotors() {
		motorRight.set(0);
		motorLeft.set(0);
	}

	public void calcDist() {
		velX = velX + AccelX;
		velY = velY + AccelY;
		velZ = velZ + AccelZ;
		
		distX = velX * Time.get();
		distY = velY * Time.get();
		distZ = velZ * Time.get();
		//in meters
	}
	public void autonomousPeriodic() {
		if (processStep == 1) {
			//once upon a time, there was a goat
			motorLeft.set(0.5);
			motorRight.set(-0.5);
			Timer.delay(2);
			motorLeft.set(0);
			motorRight.set(0);
			Timer.delay(0.25);
			processStep = 200;
		}
		else if (processStep == 200) {
			//once upon a time, there was a goat
			motorLeft.set(-0.5);
			motorRight.set(0.5);
			Timer.delay(0.5);
			motorLeft.set(0);
			motorRight.set(0);
			Timer.delay(0.25);
			processStep = 2;
		}
		else if (processStep == 2) {
			if (AngleZ > -30) {
				motorLeft.set(-0.5);
				motorRight.set(-0.5);
			}
			else { 
				zeroMotors();
				processStep = 3;
			}
		}
		else if (processStep == 3) {
			Timer.delay(0.25);
			motorLeft.set(0.5);
			motorRight.set(-0.5);
			Timer.delay(0.75);
			motorLeft.set(0);
			motorRight.set(0);
			processStep = 4;
		}
		else if (processStep == 4) {
			Timer.delay(0.25);
			pinch.set(-0.5);
			Timer.delay(0.75);
			pinch.set(0);
			processStep = 5;
		}
	}
	
	/*
	.....                           ..                                     
 .H8888888h.  ~-.             x .d88"                                      
 888888888888x  `>             5888R                     u.    .d``        
X~     `?888888hx~      .u     '888R        .u     ...ue888b   @8Ne.   .u  
'      x8.^"*88*"    ud8888.    888R     ud8888.   888R Y888r  %8888:u@88N 
 `-:- X8888x       :888'8888.   888R   :888'8888.  888R I888>   `888I  888.
      488888>      d888 '88%"   888R   d888 '88%"  888R I888>    888I  888I
    .. `"88*       8888.+"      888R   8888.+"     888R I888>    888I  888I
  x88888nX"      . 8888L        888R   8888L      u8888cJ888   uW888L  888'
 !"*8888888n..  :  '8888c. .+  .888B . '8888c. .+  "*888*P"   '*88888Nu88P 
'    "*88888888*    "88888%    ^*888%   "88888%      'Y"      ~ '88888F`   
        ^"***"`       "YP'       "%       "YP'                   888 ^     
                                                                 *8E       
                                                                 '8>       
                                                                  "        

	 */
	
	public void teleopInit() {
		updateThings();
		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 1);
		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 1);
		Timer.delay(1);
		masterRemote.setRumble(Joystick.RumbleType.kRightRumble, 0);
		masterRemote.setRumble(Joystick.RumbleType.kLeftRumble, 0);
	}
	
	public void teleopPeriodic() {
		
		
		updateThings();
		
		/*if (JoyButtonRight == true && JoyRightToggle  == false) {
			CameraServer.getInstance().startAutomaticCapture(1);
			JoyRightToggle = true;
		}*/
		/*else if (JoyButtonRight == true && JoyRightToggle == true) {
			CameraServer.getInstance().startAutomaticCapture(0);
			JoyRightToggle = false;
		}*/
		
		if((ButtonB == true) && (fastMode == false)){
			fastMode = true;
		}
		else if((fastMode == true) && (ButtonB == true)) {
			fastMode = false;
		}
		if(fastMode == true){
			driveSpeed = 1;
		}
		else if(fastMode == false){
			driveSpeed = 0.5;
		}
		
		if ((ButtonBack == true) && (toggleDriveMode == true)) {
			toggleDriveMode = false;
		}
		else if ((ButtonBack == true) && (toggleDriveMode == false)) {
			toggleDriveMode = true;
		}
		
		if ((ButtonA == true) && (toggleAscenderMode == true)) {
			toggleAscenderMode = false;
		}
		else if ((ButtonA == true) && (toggleAscenderMode == false)) {
			toggleAscenderMode = true;
		}
		
		if(toggleDriveMode){
			//Tank drive:
			motorLeft.set((-1 * driveSpeed) * AxisControlLeftY);
			motorOtherLeft.set((-1 * driveSpeed) * AxisControlLeftY);
			motorRight.set(driveSpeed * AxisControlRightY);
			motorOtherRight.set(driveSpeed * AxisControlRightY);
			
			if (toggleAscenderMode) {
				if     ((TriggerRight > 0) && (armStopper1.get() == false)) arm.set(0.5 * TriggerRight);
				else if ((TriggerLeft > 0) && (armStopper2.get() == false)) arm.set(-0.5 * TriggerLeft);
				else arm.set(0);
			}
			else {
				if (TriggerRight > 0) winch.set(-1 * TriggerRight);
				else if (TriggerLeft > 0) winch.set(-0.3 * TriggerLeft);
				else winch.set(0);
			}
			
			if (BumperLeft == true ) {
				pinch.set(-1);
				Timer.delay(0.01);
				pinch.set(-0.7);
				APressed = true;
			}
			else if (BumperRight == true ) {
				pinch.set(1);
				Timer.delay(0.01);
				pinch.set(0.7);
				APressed = false;
			}
			else {
				pinch.set(0);
			}
			
		} 
		else {
			//Arcade drive:
			motorLeft.set(     (      (driveSpeed)) * (AxisControlLeftY - AxisControlLeftX));
			motorOtherLeft.set((      (driveSpeed)) * (AxisControlLeftY - AxisControlLeftX));
			motorRight.set((     -1 * (driveSpeed)) * (AxisControlLeftY + AxisControlLeftX));
			motorOtherRight.set((-1 * (driveSpeed)) * (AxisControlLeftY + AxisControlLeftX));
			//Grabber
			
			if (toggleAscenderMode) {
				if     ((TriggerRight > 0) && (armStopper1.get() == false)) arm.set(0.5 * TriggerRight);
				else if ((TriggerLeft > 0) && (armStopper2.get() == false)) arm.set(-0.5 * TriggerLeft);
				else arm.set(0);
			}
			else {
				if (TriggerRight > 0) winch.set(-1 * TriggerRight);
				else if (TriggerLeft > 0) winch.set(-0.3 * TriggerLeft);
				else winch.set(0);
			}
			
			if (BumperLeft == true ) {
				pinch.set(-1);
				Timer.delay(0.01);
				pinch.set(-0.7);
				APressed = true;
			}
			else if (BumperRight == true ) {
				pinch.set(1);
				Timer.delay(0.01);
				pinch.set(0.7);
				APressed = false;
			}
			else {
				pinch.set(0);
			}
			
		}
		
	}
	
}

