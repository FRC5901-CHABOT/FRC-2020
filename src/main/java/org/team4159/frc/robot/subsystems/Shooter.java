package org.team4159.frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import static org.team4159.frc.robot.Constants.*;

public class Shooter extends SubsystemBase {
  private TalonSRX primary_shooter_talon;

  private TalonSRX configureTalonSRX(TalonSRX talonSRX) {
    talonSRX.configFactoryDefault();
    talonSRX.setNeutralMode(NeutralMode.Coast);

    return talonSRX;
  }

  private VictorSPX configureVictorSPX(VictorSPX victorSPX) {
    victorSPX.configFactoryDefault();
    victorSPX.setNeutralMode(NeutralMode.Coast);

    return victorSPX;
  }

  public Shooter() {
    TalonSRX shooter_talon_two;
    VictorSPX shooter_victor_one, shooter_victor_two;

    primary_shooter_talon = configureTalonSRX(new TalonSRX(CAN_IDS.PRIMARY_SHOOTER_TALON_ID));
    shooter_talon_two = configureTalonSRX(new TalonSRX(CAN_IDS.SHOOTER_TALON_TWO_ID));

    shooter_victor_one = configureVictorSPX(new VictorSPX(CAN_IDS.SHOOTER_VICTOR_ONE_ID));
    shooter_victor_two = configureVictorSPX(new VictorSPX(CAN_IDS.SHOOTER_VICTOR_TWO_ID));

    shooter_talon_two.follow(primary_shooter_talon);
    shooter_victor_one.follow(primary_shooter_talon);
    shooter_victor_two.follow(primary_shooter_talon);

    SmartDashboard.putNumber("target_shooter_speed", 0);
    SmartDashboard.putNumber("shooter_kp", SHOOTER_CONSTANTS.kP);
    SmartDashboard.putNumber("shooter_ki", SHOOTER_CONSTANTS.kI);
    SmartDashboard.putNumber("shooter_kd", SHOOTER_CONSTANTS.kD);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("current_shooter_speed", getVelocity());
    setP(SmartDashboard.getNumber("shooter_kp", SHOOTER_CONSTANTS.kP));
    setI(SmartDashboard.getNumber("shooter_ki", SHOOTER_CONSTANTS.kI));
    setD(SmartDashboard.getNumber("shooter_kd", SHOOTER_CONSTANTS.kD));
  }

  public void setRawSpeed(double percent) {
    primary_shooter_talon.set(ControlMode.PercentOutput, percent);
  }

  public void setTargetSpeed(double speed) {
    primary_shooter_talon.set(ControlMode.Velocity, speed);
  }

  public void stop() {
    setRawSpeed(0);
  }

  public void setP(double kP) {
    primary_shooter_talon.config_kP(0, kP);
  }

  public void setI(double kI) {
    primary_shooter_talon.config_kI(0, kI);
  }

  public void setD(double kD) {
    primary_shooter_talon.config_kD(0, kD);
  }

  public double getVelocity() {
    return primary_shooter_talon.getSelectedSensorVelocity();
  }
}
