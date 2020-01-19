package org.team4159.frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import org.team4159.frc.robot.subsystems.Drivetrain;

/*
 * Placeholder until we plan out an autonomous routine
 */
public class BlueAuto extends SequentialCommandGroup {
  public BlueAuto(Drivetrain drivetrain) {
    addRequirements(drivetrain);

    addCommands(
      // start position to trench run
      // new FollowTrajectory(Trajectories.TRENCH_RUN_BALL_TO_SHOOTING_POS, drivetrain)
      // shoot ball
    );
  }
}
