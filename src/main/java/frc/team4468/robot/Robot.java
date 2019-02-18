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
  public static SuperStructure struc;
  public static Cargo cargo;
  public static Drive drive;
  public static Hatch hatch;

  // MANAGERS
  private MacroExecutor executor_;
  private SubsystemManager sm_;

  // CONTROLLERS
  public static JoystickRunner rightDrive = new JoystickRunner(Constants.Input.driveRight);
  public static JoystickRunner leftDrive  = new JoystickRunner(Constants.Input.driveLeft);
  public static XboxRunner operator = new XboxRunner(Constants.Input.operator);

  // ROBOT
  @Override public void robotInit() {
    struc = new SuperStructure();
    cargo = new Cargo();
    hatch = new Hatch();
    drive = new Drive();

    sm_ = new SubsystemManager(
      struc,
      cargo,
      hatch,
      drive
    );

    executor_ = new MacroExecutor(4);
  }
 
  @Override public void teleopInit() { start(); }
  @Override public void autonomousInit() { start(); }
  @Override public void teleopPeriodic() { periodic(); }
  @Override public void autonomousPeriodic() { periodic(); }


  @Override public void testInit() {}
  @Override public void testPeriodic() {}

  private void start(){
    sm_.start();
  }

  private void periodic(){
    // Drive
    drive.setTank(-leftDrive.getY(), -rightDrive.getY());
    leftDrive.whenTriggerPressed(() -> drive.setGear(true));
    rightDrive.whenTriggerPressed(() -> drive.setGear(true));

    // Operator
    cargo.setIntake(operator.getY(Hand.kLeft));
    operator.whenPressed(6, () -> hatch.setGear(true));
    operator.whenPressed(5, () -> hatch.setGear(false));
    operator.whenPressed(4, () -> cargo.setAngle(130));
    operator.whenPressed(2, () -> cargo.setAngle(85));
    operator.whenPressed(1, () -> cargo.setAngle(90));
  }
}
