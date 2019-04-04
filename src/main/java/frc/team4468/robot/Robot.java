package frc.team4468.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.team4468.robot.Lib.SubsystemManager;
import frc.team4468.robot.Lib.Input.XboxRunner;
import frc.team4468.robot.Lib.Actions.MacroExecutor;
import frc.team4468.robot.Subs.*;
import frc.team4468.robot.Auto.Actions.*;

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
  public static XboxRunner driver = new XboxRunner(Constants.Input.drive);
  //public static JoystickRunner rightDrive = new JoystickRunner(Constants.Input.driveRight);
  //public static JoystickRunner leftDrive  = new JoystickRunner(Constants.Input.driveLeft);
  public static XboxRunner operator = new XboxRunner(Constants.Input.operator);

  // PDP
  private PowerDistributionPanel pdp_ = new PowerDistributionPanel();
  private Compressor comp_ = new Compressor();

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
    UsbCamera cam = CameraServer.getInstance().startAutomaticCapture("Camera 1", "/dev/video1");
    cam.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);
    UsbCamera cam2 = CameraServer.getInstance().startAutomaticCapture("Camera 2", "/dev/video0");
    cam2.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);

  }
  @Override public void robotPeriodic() {
    sm_.log();
  }
  

  @Override public void teleopInit() { start(); }
  @Override public void autonomousInit() { start(); }
  @Override public void teleopPeriodic() { periodic(); }
  @Override public void autonomousPeriodic() { periodic(); }


  @Override public void testInit() {}
  @Override public void testPeriodic() {
    hatch.log();
  }

  private void start(){
    //struc.start();
    sm_.start();
    comp_.clearAllPCMStickyFaults();
  }

  private void periodic(){
    sm_.update();
    
    
    // Drive
    drive.setArcade(-driver.getX(Hand.kLeft), -driver.getY(Hand.kRight));
    driver.whenTriggerThreshold(Hand.kLeft, .9, () -> drive.setGear(true));
    driver.whenTriggerThreshold(Hand.kRight, .9, () -> drive.setGear(false));

    // Operator
    if(operator.getRawButton(6)){
      operator.whenPressed(4, () -> cargo.setAngle(170));
      operator.whenPressed(3, () -> cargo.setAngle(145));
      operator.whenPressed(2, () -> cargo.setAngle(100));
      operator.whenPressed(1, () -> cargo.setAngle(90));
    } else {
      operator.whenPressed(8, () -> hatch.setAngle(230));
      operator.whenPressed(4, () -> hatch.setAngle(220));
      operator.whenPressed(2, () -> executor_.execute("Hatch Delivery", new HatchDelivery()));
      operator.whenPressed(3, () -> hatch.toggleClamp());;
      operator.whenPressed(1, () -> hatch.setAngle(180));
    }
    operator.whenPressed(5, () -> cargo.lock());
    
    if(operator.getTriggerAxis(Hand.kLeft) > .75){
      cargo.setIntake(-.8);
    } else if(operator.getTriggerAxis(Hand.kRight) > .75){
      cargo.setIntake(.75);
    } else {
      cargo.setIntake(.15);
    }
  }
}
