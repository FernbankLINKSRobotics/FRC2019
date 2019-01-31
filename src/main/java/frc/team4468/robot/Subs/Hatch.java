package frc.team4468.robot.Subs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;

public class Hatch implements Subsystem {
    private WPI_TalonSRX rotator_ = new WPI_TalonSRX(Constants.Hatch.rotator);
    private DoubleSolenoid popper_ = new DoubleSolenoid(Constants.Hatch.pop1, Constants.Hatch.pop2);
    private DigitalInput limit_ = new DigitalInput(Constants.Hatch.zeroer);
    
    public enum Position {
        ZEROING,
        OTHER, // Mainly for testing
        GROUND, // 0
        VERTICLE, // 90
        RETRACTED // 135
    };

    private Position state_ = Position.RETRACTED;
    private Value pop_ = Value.kReverse;
    private boolean zeroed_ = false;
    private double angle_ = -1;

    private double pErr_ = 0; // Previous error

    public Hatch(){
        rotator_.configFactoryDefault();
        rotator_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        rotator_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        //rotator_.config_kD(0, 0);
    }

    public void setAngle(Position state){ state_ = state; }

    public void setAngle(double angle){
        state_ = Position.OTHER;
        angle_ = angle;
    }

    public void setPop(boolean pop){ pop_ = (pop) ? Value.kForward : Value.kReverse; }

    public double armPDF(double set, double angle){
        double err = set - angle;
        double o = (Constants.Hatch.kP * err) +                                   // Power proportinal to error
                   (Constants.Hatch.kD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Hatch.kF * Math.cos(angle * (Math.PI/ 180)));       // Power to counteract gravity
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return o;
    }

    @Override public void start(){
        state_ = Position.ZEROING;
        pop_ = Value.kReverse;
    }

    @Override public void update(){
        if(!zeroed_) { state_ = Position.ZEROING; }
        if(popper_.get() != pop_){ popper_.set(pop_); }

        switch(state_){
            case ZEROING:
                rotator_.set(ControlMode.PercentOutput, Constants.Hatch.zeroSpeed);
                if(limit_.get()) {
                    state_ = Position.RETRACTED;
                    zeroed_ = true;
                }
                break;
            case RETRACTED: angle_ = 135;
            case VERTICLE: angle_ = 90;
            case GROUND: angle_ = 0;
            default:
                // TODO: controls arm to angle (all that math shiz)
                //rotator_.set(ControlMode.MotionProfileArc, angle_);
                rotator_.set(ControlMode.PercentOutput, armPDF(angle_, rotator_.getSelectedSensorPosition()));
        }
    }

    @Override public void stop(){
        rotator_.stopMotor();
        popper_.set(Value.kReverse);
    }

    @Override public void log(){
        SmartDashboard.putBoolean("IS ZEROING", state_ == Position.ZEROING);
        SmartDashboard.putBoolean("IS POPPED", popper_.get() == Value.kForward);
        SmartDashboard.putNumber("ANGLE", angle_);
        SmartDashboard.putNumber("Motor speed", rotator_.get());
    }
}