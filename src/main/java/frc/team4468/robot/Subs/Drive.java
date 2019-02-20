package frc.team4468.robot.Subs;

import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive implements Subsystem {
    // HARDWARE
    private WPI_TalonSRX leftMaster_ = new WPI_TalonSRX(Constants.Drive.leftMaster);
    private WPI_VictorSPX leftSlave1_ = new WPI_VictorSPX(Constants.Drive.leftSlave1);
    private WPI_VictorSPX leftSlave2_ = new WPI_VictorSPX(Constants.Drive.leftSlave2);
    private WPI_TalonSRX rightMaster_ = new WPI_TalonSRX(Constants.Drive.rightMaster);
    private WPI_VictorSPX rightSlave1_ = new WPI_VictorSPX(Constants.Drive.rightSlave1);
    private WPI_VictorSPX rightSlave2_ = new WPI_VictorSPX(Constants.Drive.rightSlave2);

    private DoubleSolenoid shifter_ = new DoubleSolenoid(Constants.Drive.shift1, 
                                                         Constants.Drive.shift2);

    /*
    private DifferentialDrive drive_ = new DifferentialDrive(
        new SpeedControllerGroup(
            leftMaster_,
            leftSlave1_,
            leftSlave2_),
        new SpeedControllerGroup(
            rightMaster_,
            rightSlave1_,
            rightSlave2_)
    );
    */
    private DifferentialDrive drive_ = new DifferentialDrive(leftMaster_, rightMaster_);

    // STATE VARIABLES
    private Value shift_ = Value.kOff;
    private boolean isTank = false;
    private double modifier_ = 1;
    private double lpower_ = 0;
    private double rpower_ = 0;
    private double speed_ = 0;
    private double turn_ = 0;

    // CONSTRUCTOR
    public Drive(){
        leftMaster_.configFactoryDefault();
        leftSlave1_.configFactoryDefault();
        leftSlave2_.configFactoryDefault();
        rightMaster_.configFactoryDefault();
        rightSlave1_.configFactoryDefault();
        rightSlave2_.configFactoryDefault();

        leftSlave1_.follow(leftMaster_);
        leftSlave2_.follow(leftMaster_);
        rightSlave1_.follow(rightMaster_);
        rightSlave2_.follow(rightMaster_);

        leftMaster_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightMaster_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftMaster_.enableCurrentLimit(true);
        leftMaster_.configPeakCurrentLimit(40);
        leftMaster_.configContinuousCurrentLimit(40);

        rightMaster_.enableCurrentLimit(true);
        rightMaster_.configPeakCurrentLimit(40);
        rightMaster_.configContinuousCurrentLimit(40);

        leftMaster_.configOpenloopRamp(Constants.Drive.rampRate, 
                                       Constants.System.CANTimeout);
        leftSlave1_.configOpenloopRamp(Constants.Drive.rampRate,
                                       Constants.System.CANTimeout);
        leftSlave2_.configOpenloopRamp(Constants.Drive.rampRate,
                                       Constants.System.CANTimeout);
        rightMaster_.configOpenloopRamp(Constants.Drive.rampRate,
                                        Constants.System.CANTimeout);
        rightSlave1_.configOpenloopRamp(Constants.Drive.rampRate,
                                        Constants.System.CANTimeout);
        rightSlave2_.configOpenloopRamp(Constants.Drive.rampRate,
                                        Constants.System.CANTimeout);
    }

    // PUBLIC INPUT OUTPUT
    public void setArcade(double turn, double speed){
        turn_ = turn;
        speed_ = speed;
        isTank = false;
    }
    public void setTank(double right, double left){
        rpower_ = right;
        lpower_ = left;
        isTank = true;
    }

    public double getRightVel(){
        return rightMaster_.getSelectedSensorVelocity(0) * 
               ((360 * Constants.Drive.wheelDiameter)/4096);
    }

    public int getLeftVel() {
        return leftMaster_.getSelectedSensorVelocity(0);
    }

    public void toggle(){
        shift_ = (shift_ == Value.kForward) ? Value.kReverse : Value.kForward;
    }

    public void setGear(boolean high){
        shift_ = (high) ? Value.kForward : Value.kReverse;
    }

    public void setModifier(double mod){
        modifier_ = mod;
    }

    // SUBSYSTEM IMPL
    @Override public void start(){
        shift_ = Value.kForward;
        shifter_.set(shift_);
    }

    @Override public void update(){
        if(isTank){
            drive_.tankDrive(modifier_ * lpower_, 
                             modifier_ * rpower_);
        } else {
            drive_.arcadeDrive(modifier_ * speed_,
                               modifier_ * turn_);
        }
        if(shifter_.get() != shift_) { shifter_.set(shift_); }
    }

    @Override public void stop(){
        drive_.stopMotor();
        shifter_.set(Value.kForward);
    }

    @Override public void log(){
    }
}