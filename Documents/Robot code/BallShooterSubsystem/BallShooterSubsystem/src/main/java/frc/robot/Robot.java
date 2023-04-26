/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //these are temporary variables for the purpose of writing code
  Joystick stick = new Joystick(0);
  Joystick flightStick = new Joystick(1);

  //Shooter Angle Motor
  Spark shooterAngle = new Spark(7);
  //Spinning Shooter Disks Motor
  Spark shooterDisks = new Spark(6);
  //TODO - edit ports for spark motors

  //Shooter Angle Encoder
  Encoder angleEncoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
  //TODO - edit (0, 1,..) with real DIO ports if needed
  //angleEncoder output
  double angleOutput = (angleEncoder.get() / 5.0) * 360;

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    angleEncoder.setDistancePerPulse(4./256.);
    angleEncoder.setSamplesToAverage(5);
    angleEncoder.reset();

    errorSum = 0;
    lastError = 0;
    lastTimeStamp = Timer.getFPGATimestamp();
  }
  //Variables for PID
  double setPoint = 0;
  double errorSum = 0;
  double lastTimeStamp = 0;
  double lastError = 0;
  final double kP = .05;
  final double kI = .5;
  final double kD = .5;
  final double iLimit = 5;

  @Override
  public void robotPeriodic() {
    //Get joystick command
    if (flightStick.getRawButtonPressed(4)) {
     setPoint = 45;
    }
   
    if (flightStick.getRawButtonPressed(5)) {
      setPoint = 67;
    }

    if (flightStick.getRawButtonPressed(6)) {
      setPoint = 90;
    }
   
    //Calculations
    double error = setPoint - angleOutput;
    double dt = Timer.getFPGATimestamp() - lastTimeStamp;
    if (Math.abs(error) < iLimit) {
      errorSum += error*dt;
    }
    double errorRate = (error - lastError) / dt;
    double outputSpeed = kP*error + kI*errorSum + kD*errorRate;
    //Output to motors
    shooterAngle.set(outputSpeed);
    //Update last variables
    lastTimeStamp = Timer.getFPGATimestamp();
    lastError = error;
  }

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopPeriodic() {
    //shoots bal when the trigger is pressed
    if (flightStick.getRawButton(1)){
      shooterDisks.set(.75);
    }else {
      shooterDisks.set(0);
    }
  }

  

  @Override
  public void testPeriodic() {
  }
}
