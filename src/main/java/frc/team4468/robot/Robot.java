package frc.team4468.robot;

import edu.wpi.first.wpilibj.TimedRobot;
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
      //struc,
      cargo,
      hatch,
      drive
    );

    executor_ = new MacroExecutor(4);
  }
  @Override public void robotPeriodic() {
    sm_.log();
  }
  

  @Override public void teleopInit() { start(); }
  @Override public void autonomousInit() { start(); }
  @Override public void teleopPeriodic() { periodic(); }
  @Override public void autonomousPeriodic() { periodic(); }


  @Override public void testInit() {}
  @Override public void testPeriodic() {}

  private void start(){
    sm_.start();
    sm_.update();

    //hatch.reset();
  }

  private void periodic(){
    sm_.update();
    
    // Drive
    drive.setTank(-leftDrive.getY(), -rightDrive.getY());
    leftDrive.whenTriggerPressed(() -> drive.setGear(true));
    rightDrive.whenTriggerPressed(() -> drive.setGear(true));

    // Operator
    //cargo.setIntake(operator.getY(Hand.kLeft));
    operator.whenPressed(5, () -> {
      hatch.togglePop();
    });
    if(operator.getRawButton(6)){
      //System.out.println("PRESSED");
      operator.whenPressed(4, () -> cargo.setAngle(150));
      operator.whenPressed(3, () -> cargo.setAngle(130));
      operator.whenPressed(2, () -> cargo.setAngle(90));
      operator.whenPressed(1, () -> cargo.setAngle(85));
    } else {
      operator.whenPressed(4, () -> hatch.setAngle(220));
      operator.whenPressed(3, () -> hatch.setAngle(175));
      operator.whenPressed(2, () -> hatch.setAngle(165));
      operator.whenPressed(1, () -> hatch.setAngle(100));
    }
    if(operator.getTriggerAxis(Hand.kLeft) > .75){
      cargo.setIntake(-1);
    } else if(operator.getTriggerAxis(Hand.kRight) > .75){
      cargo.setIntake(.75);
    } else {
      cargo.setIntake(0);
    }
  }
}
