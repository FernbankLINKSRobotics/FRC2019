package frc.team4468.robot.Subs;

import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;
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

    // STATE VARIABLES
    private boolean isTank = false;
    private double turn_ = 0;
    private double speed_ = 0;
    private double lpower_ = 0;
    private double rpower_ = 0;
    private Value shift_ = Value.kOff;

    // CONSTRUCTOR
    public Drive(){
        /*
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

        leftMaster_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        leftMaster_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        rightMaster_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        rightMaster_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftMaster_.setInverted(true);
        rightMaster_.setInverted(false);
        leftSlave1_.setInverted(InvertType.FollowMaster);
        leftSlave2_.setInverted(InvertType.FollowMaster);
        rightSlave1_.setInverted(InvertType.FollowMaster);
        rightSlave2_.setInverted(InvertType.FollowMaster);
        */

        leftMaster_.enableCurrentLimit(true);
        leftMaster_.configPeakCurrentLimit(40);
        leftMaster_.configContinuousCurrentLimit(40);

        rightMaster_.enableCurrentLimit(true);
        rightMaster_.configPeakCurrentLimit(40);
        rightMaster_.configContinuousCurrentLimit(40);

        leftMaster_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        leftSlave1_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        leftSlave2_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        rightMaster_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        rightSlave1_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        rightSlave2_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
    }

    // INPUT OUTPUT
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

    public void toggle(){
        shift_ = (shift_ == Value.kForward) ? Value.kReverse : Value.kForward;
    }

    public void setGear(boolean high){
        shift_ = (high) ? Value.kForward : Value.kReverse;
    }

    // SUBSYSTEM IMPL
    @Override public void start(){
        //shift_ = Value.kReverse;
        //shifter_.set(shift_);
    }

    @Override public void update(){
        if(isTank){
            drive_.tankDrive(lpower_, rpower_);
        } else {
            drive_.arcadeDrive(speed_, turn_);
        }
        if(shifter_.get() != shift_) { shifter_.set(shift_); }
    }

    @Override public void stop(){
        drive_.stopMotor();
        //shifter_.set(Value.kReverse);
    }
    @Override public void log(){
    }
}