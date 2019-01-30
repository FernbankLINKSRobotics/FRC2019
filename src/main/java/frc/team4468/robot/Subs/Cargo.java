package frc.team4468.robot.Subs;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;

public class Cargo implements Subsystem {
    private WPI_VictorSPX intake_ =  new WPI_VictorSPX(Constants.Cargo.intake);
    private WPI_TalonSRX rotator_ = new WPI_TalonSRX(Constants.Cargo.rotator);
    private DigitalInput limit_ = new DigitalInput(Constants.Hatch.zeroer);


    public enum Position {
        ZEROING,
        OTHER, // Mainly for testing
        GROUND, // 0
        SHIP, //45
        ROCKET, //80
        RETRACTED //180
    };

    private Position state_ = Position.RETRACTED;
    private double angle_ = 0;
    private double speed_ = 0;

    private double pErr_ = 0;

    public Cargo() {
        rotator_.configFactoryDefault();
        rotator_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        rotator_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    }

    public void setAngle(Position state){ state_ = state; }

    public void setAngle(double angle){
        state_ = Position.OTHER;
        angle_ = angle;
    }

    public void setIntake(double speed){ speed_ = speed; }

    public double armPDF(double set, double angle){
        double err = set - angle;
        double o = (Constants.Cargo.kP * err) +                                   // Power proportinal to error
                   (Constants.Cargo.kD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Cargo.kF * Math.cos(angle * (Math.PI/ 180)));       // Power to counteract gravity
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return o;
    }
    
    @Override public void start(){
        state_ = Position.ZEROING;
        angle_ = 0;
    }

    @Override public void update(){
        switch(state_) { 
            case ZEROING:
                rotator_.set(ControlMode.PercentOutput, -0.2);
                if(limit_.get()) { state_ = Position.RETRACTED; }
                break;
            case RETRACTED: angle_ = 180;
            case ROCKET: angle_ = 80;
            case SHIP: angle_ = 45;
            default:
                //TODO: control arm to angle (all that math shiz) 
                rotator_.set(ControlMode.PercentOutput, armPDF(angle_, rotator_.getSelectedSensorPosition())); 
        }
    }

    @Override public void stop(){
        rotator_.stopMotor();
        intake_.stopMotor();
    }

    @Override public void log(){
        /*
        SmartDashboard.putBoolean("IS ZEROING", state_ == Position.ZEROING);
        SmartDashboard.putNumber("ANGLE", angle_);
        SmartDashboard.putNumber("Motor Speed", rotator.get());
        */
    }
}