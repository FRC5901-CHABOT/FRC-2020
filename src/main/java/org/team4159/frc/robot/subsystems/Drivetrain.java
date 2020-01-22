package org.team4159.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.team4159.frc.robot.Constants.*;

public class Drivetrain extends SubsystemBase {
  private TalonFX left_front_falcon, left_rear_falcon, right_front_falcon, right_rear_falcon;
  private SpeedControllerGroup left_falcons;
  private SpeedControllerGroup right_falcons;

  private DifferentialDrive differential_drive;

  private DifferentialDriveOdometry odometry;
  private PigeonIMU pigeon;

  private boolean is_oriented_forward = true;

  private double dx = 0;
  private double dy = 0;
  private double prev_magnitude = 0;

  public Drivetrain() {
    left_front_falcon = configureTalonFX(new WPI_TalonFX(CAN_IDS.LEFT_FRONT_FALCON_ID));
    left_rear_falcon = configureTalonFX(new WPI_TalonFX(CAN_IDS.LEFT_REAR_FALCON_ID));
    right_front_falcon = configureTalonFX(new WPI_TalonFX(CAN_IDS.RIGHT_FRONT_FALCON_ID));
    right_rear_falcon = configureTalonFX(new WPI_TalonFX(CAN_IDS.RIGHT_REAR_TALON_ID));

    left_front_falcon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    right_front_falcon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

    // setSensorPhase isn't working

    left_falcons = new SpeedControllerGroup(
      (WPI_TalonFX) left_front_falcon,
      (WPI_TalonFX) left_rear_falcon);
    right_falcons = new SpeedControllerGroup(
      (WPI_TalonFX) right_front_falcon,
      (WPI_TalonFX) right_rear_falcon);
    left_falcons.setInverted(true);
    right_falcons.setInverted(true);

    pigeon = new PigeonIMU(CAN_IDS.PIGEON_ID);

    differential_drive = new DifferentialDrive(left_falcons, right_falcons);
    odometry = new DifferentialDriveOdometry(new Rotation2d(0));

    zeroSensors();
  }

  public void flipOrientation() {
    is_oriented_forward = !is_oriented_forward;
  }

  @Override
  public void periodic() {
    odometry.update(
      Rotation2d.fromDegrees(getDirection()),
      getLeftDistance(),
      getRightDistance()
    );

    SmartDashboard.putNumber("X", getPose().getTranslation().getX());
    SmartDashboard.putNumber("Y", getPose().getTranslation().getY());
    SmartDashboard.putNumber("Angle", getDirection());
    SmartDashboard.putNumber("Left Encoder", getLeftDistance());
    SmartDashboard.putNumber("Right Encoder", getRightDistance());
  }

  private TalonFX configureTalonFX(TalonFX talonSRX) {
    talonSRX.configFactoryDefault();
    talonSRX.setNeutralMode(NeutralMode.Coast);

    return talonSRX;
  }

  public void tankDrive(double left, double right) {
    if (is_oriented_forward) {
      differential_drive.tankDrive(left, right);
    } else {
      differential_drive.tankDrive(-right, -left);
    }
  }

  public void arcadeDrive(double forward, double rotation) {
    if (is_oriented_forward) {
      differential_drive.arcadeDrive(forward, rotation);
    } else {
      differential_drive.arcadeDrive(-forward, -rotation);
    }
  }

  public void rawDrive(double left, double right) {
    left_falcons.set(left);
    right_falcons.set(right);
  }

  public void voltsDrive(double left_volts, double right_volts) {
    if (is_oriented_forward) {
      left_falcons.setVoltage(left_volts);
      right_falcons.setVoltage(right_volts);
    } else {
      left_falcons.setVoltage(-right_volts);
      right_falcons.setVoltage(-left_volts);
    }
  }

  public void stop() {
    rawDrive(0, 0);
  }

  public void resetEncoders() {
    left_front_falcon.setSelectedSensorPosition(0);
    right_front_falcon.setSelectedSensorPosition(0);
  }

  public void resetDirection() {
    pigeon.setFusedHeading(0);
  }

  public void zeroSensors() {
    resetEncoders();
    resetDirection();
    odometry.resetPosition(
      new Pose2d(new Translation2d(0, 0), Rotation2d.fromDegrees(0)),
      Rotation2d.fromDegrees(0)
    );
  }

  public double getLeftVoltage() {
    return left_front_falcon.getMotorOutputVoltage();
  }

  public double getRightVoltage() {
    return right_front_falcon.getMotorOutputVoltage();
  }

  // distance in meters
  public double getLeftDistance() {
    return left_front_falcon.getSelectedSensorPosition() * DRIVE_CONSTANTS.METERS_PER_TICK;
  }

  // velocity in meters / sec
  public double getLeftVelocity() {
    return left_front_falcon.getSelectedSensorVelocity() * DRIVE_CONSTANTS.METERS_PER_TICK;
  }

  public double getRightDistance() {
    return -1 * right_front_falcon.getSelectedSensorPosition() * DRIVE_CONSTANTS.METERS_PER_TICK;
  }

  public double getRightVelocity() {
    return -1 * right_front_falcon.getSelectedSensorVelocity() * DRIVE_CONSTANTS.METERS_PER_TICK;
  }

  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(getLeftVelocity(), getRightVelocity());
  }

  public double getDirection() {
    return Math.IEEEremainder(pigeon.getFusedHeading(), 360) * -1;
  }
}