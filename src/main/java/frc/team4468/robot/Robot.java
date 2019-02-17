package frc.team4468.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.team4468.robot.Lib.Input.JoystickRunner;
import frc.team4468.robot.Lib.SubsystemManager;
import frc.team4468.robot.Lib.Input.XboxRunner;
import frc.team4468.robot.Lib.Actions.MacroExecutor;
import frc.team4468.robot.Subs.*;

public class Robot extends TimedRobot {
  // SUBSYSTEMS
  public static Cargo cargo;
  public static Drive drive;
  public static Hatch hatch;

  // MANAGERS
  private MacroExecutor executor_;
  private SubsystemManager sm_;

  // CONTROLLERS
  public static XboxRunner operator = new XboxRunner(Constants.Input.operator);
  public static JoystickRunner leftDrive = new JoystickRunner(Constants.Input.driveLeft);
  public static JoystickRunner rightDrive = new JoystickRunner(Constants.Input.driveRight);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    cargo = new Cargo();
    hatch = new Hatch();
    drive = new Drive();

    sm_ = new SubsystemManager(
      cargo,
      hatch,
      drive
    );

    executor_ = new MacroExecutor(4);
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

  @Override public void teleopInit() {
    //hatch.setGear(true);
  }
  @Override public void teleopPeriodic() {
    // Drive
    drive.setTank(leftDrive.getY(), rightDrive.getY());
    leftDrive.whenTriggerPressed(() -> drive.setGear(true));
    rightDrive.whenTriggerPressed(() -> drive.setGear(true));

    // Operator
    cargo.setPower(operator.getY(Hand.kLeft));
    cargo.setIntake(-1 * operator.getTriggerAxis(Hand.kLeft));
    cargo.setIntake(operator.getTriggerAxis(Hand.kRight));
    operator.whenPressed(4, () -> hatch.setGear(true));
    operator.whenPressed(5, () -> hatch.setGear(false));
  }

  @Override public void testInit() {}
  @Override public void testPeriodic() {}
}
