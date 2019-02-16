package frc.team4468.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.team4468.robot.Auto.Actions.IntakeSpeed;
import frc.team4468.robot.Auto.Actions.Pop;
import frc.team4468.robot.Lib.SubsystemManager;
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
  public static XboxController driveJoy = new XboxController(Constants.Input.driver);
  //public static Joystick opJoy = new Joystick(Constants.Input.operator);


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    cargo = new Cargo();
    //hatch = new Hatch();
    drive = new Drive();

    sm_ = new SubsystemManager(
      cargo,
      //hatch,
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

  @Override public void teleopInit() {}
  @Override public void teleopPeriodic() {
    // Drive
    drive.setTank(driveJoy.getY(Hand.kLeft), driveJoy.getY(Hand.kRight));

    // Operator
    if(driveJoy.getRawButton(5)) executor_.execute("Intake", new IntakeSpeed(-.7));
    if(driveJoy.getRawButton(6)) executor_.execute("Expel", new IntakeSpeed(1));

  }

  @Override public void testInit() {}
  @Override public void testPeriodic() {}
}
