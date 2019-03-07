package frc.team4468.robot;

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.team4468.robot.Lib.Input.JoystickRunner;
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
    /*
    Thread t = new Thread(() -> {
      boolean allowCam1 = false;
      UsbCamera cam = CameraServer.getInstance().startAutomaticCapture(0);
      //cam.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);

      UsbCamera cam2 = CameraServer.getInstance().startAutomaticCapture(1);
      //cam2.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);

      CvSink  cvSink1 = CameraServer.getInstance().getVideo(cam);
      CvSink  cvSink2 = CameraServer.getInstance().getVideo(cam2);
      CvSource outputStream = CameraServer.getInstance().putVideo("Switcher", 256, 144);

      Mat image = new Mat();

      while(!Thread.interrupted()) {
        if(operator.getRawButton(6)) {
          allowCam1 = !allowCam1;
        }
        if(allowCam1) {
          cvSink2.setEnabled(false);
          cvSink1.setEnabled(true);
          cvSink1.grabFrame(image);
        } else {
          cvSink1.setEnabled(false);
          cvSink2.setEnabled(true);
          cvSink2.grabFrame(image);
        }
        outputStream.putFrame(image);
      }
    });
    */
    //t.start();
    //UsbCamera cam = CameraServer.getInstance().startAutomaticCapture("Camera", "/dev/video0");
    //cam.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);
    UsbCamera cam2 = CameraServer.getInstance().startAutomaticCapture("Camera", "/dev/video1");
    cam2.setVideoMode(PixelFormat.kMJPEG, 265, 144, 30);

  }
  @Override public void robotPeriodic() {
    sm_.log();
  }
  

  @Override public void teleopInit() { start(); }
  @Override public void autonomousInit() { start(); }
  @Override public void teleopPeriodic() { periodic(); }
  @Override public void autonomousPeriodic() { periodic(); }


  @Override public void testInit() {
    hatch.reset();
  }
  @Override public void testPeriodic() {
    hatch.log();
  }

  private void start(){
    sm_.start();
  }

  private double t = 1;
  private double limit(double v){
    double s = Math.sin(v);
    if(Math.abs(v) > t) {
      return t * s;
    }
    return v;
  }

  private void periodic(){
    sm_.update();
    /*
    if(!cargo.zeroed()){
      cargo.update();
    } else {
      sm_.update();
    }
    */
    
    // Drive
    drive.setArcade(-limit(driver.getX(Hand.kLeft)), -limit(driver.getY(Hand.kRight)));
    driver.whenTriggerThreshold(Hand.kLeft, .9, () -> drive.setGear(true));
    driver.whenTriggerThreshold(Hand.kRight, .9, () -> drive.setGear(false));

    // Operator
    operator.whenPressed(5, () -> executor_.execute("Pop", new Pop()));
    if(operator.getRawButton(6)){
      operator.whenPressed(4, () -> cargo.setAngle(150));
      operator.whenPressed(3, () -> cargo.setAngle(120));
      operator.whenPressed(2, () -> cargo.setAngle(90));
      operator.whenPressed(1, () -> cargo.setAngle(70));
    } else {
      operator.whenPressed(4, () -> hatch.setAngle(220));
      operator.whenPressed(3, () -> hatch.setAngle(175));
      operator.whenPressed(2, () -> hatch.setAngle(165));
      operator.whenPressed(1, () -> hatch.setAngle(85));
    }
    if(operator.getTriggerAxis(Hand.kLeft) > .75){
      cargo.setIntake(-.9);
    } else if(operator.getTriggerAxis(Hand.kRight) > .75){
      cargo.setIntake(.75);
    } else {
      cargo.setIntake(.15);
    }


    System.out.println("Left Master " + pdp_.getCurrent(0));
    System.out.println("Right Master " + pdp_.getCurrent(1));
    System.out.println("Left Slave 1 " + pdp_.getCurrent(15));
    System.out.println("Left Slave 2 " + pdp_.getCurrent(14));
    System.out.println("Right Slave 1 " + pdp_.getCurrent(13));
    System.out.println("Right Slave 2 " + pdp_.getCurrent(12));

  }
}
