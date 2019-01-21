package frc.team4468.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.team4468.robot.Lib.SubsystemManager;
import frc.team4468.robot.Lib.Actions.MacroExecutor;
import frc.team4468.robot.Subs.Drive;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  // SUBSYSTEMS
  public static Drive drive;

  // CONTROLLERS
  public static MacroExecutor executor;
  private SubsystemManager sm_;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    drive = new Drive();

    sm_ = new SubsystemManager(
      drive
    );

    executor = new MacroExecutor(4);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    sm_.update();
    sm_.log();
  }

 
  @Override public void autonomousInit() {}
  @Override public void autonomousPeriodic() {}

  @Override public void teleopInit() {}
  @Override public void teleopPeriodic() {}

  @Override public void testInit() {}
  @Override public void testPeriodic() {}
}
