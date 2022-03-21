// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import frc.subsystems.AimAssist;

public class Robot extends TimedRobot {

  // Define motors
  private CANSparkMax frontLeft = new CANSparkMax(3, MotorType.kBrushless);
  private CANSparkMax frontRight = new CANSparkMax(4, MotorType.kBrushless);
  private CANSparkMax rearLeft = new CANSparkMax(5, MotorType.kBrushless);
  private CANSparkMax rearRight = new CANSparkMax(6, MotorType.kBrushless);

  // Must wait to define these to invert motors
  private MecanumDrive mecDrive;
  private AimAssist aimAssist;

  // Function to Shoot
  public void shoot(Double distance) {
    // Here, spin motors necessary to shoot a ball
    // NOTE: distance will always be -1.0, as it has not yet been implemented
  }

  @Override
  public void robotInit() {
    // Invert motors: (if needed, may change depending on how motors are mounted on robot)
    rearRight.setInverted(true);
    frontRight.setInverted(true);

    // Create MecanumDrive and AimAssist
    mecDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
    aimAssist = new AimAssist(mecDrive, this::shoot);
  }

  @Override
  public void robotPeriodic() {
    aimAssist.refresh();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // To Run in Auto:
    //aimAssist.auto(true); // to start
    //aimAssist.auto(false); // to stop
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    aimAssist.teleop();
  }
}
