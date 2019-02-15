package frc.team4468.robot.Subs;

import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive implements Subsystem {
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

    private static double turn_ = 0;
    private static double speed_ = 0;

    private static Value shift_ = Value.kOff;

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

        leftMaster_.enableCurrentLimit(true);
        leftMaster_.configPeakCurrentLimit(40);
        leftMaster_.configContinuousCurrentLimit(40);

        rightMaster_.enableCurrentLimit(true);
        rightMaster_.configPeakCurrentLimit(40);
        rightMaster_.configContinuousCurrentLimit(40);

        leftMaster_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
        leftMaster_.configOpenloopRamp(Constants.Drive.rampRate, Constants.System.CANTimeout);
    }

    public void setArcade(double turn, double speed){
        turn_ = turn;
        speed_ = speed;
    }

    public void toggle(boolean high){
        shift_ = (shift_ == Value.kForward) ? Value.kReverse : Value.kForward;
    }

    @Override public void start(){
        shift_ = Value.kReverse;
        shifter_.set(shift_);
    }

    @Override public void update(){
        drive_.arcadeDrive(speed_, turn_);
        if(shifter_.get() != shift_) { shifter_.set(shift_); }
    }

    @Override public void stop(){
        drive_.stopMotor();
        shifter_.set(Value.kReverse);
    }
    @Override public void log(){}
}