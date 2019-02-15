package frc.team4468.robot.Subs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;
import frc.team4468.robot.Lib.Control.MotionProfile;
import frc.team4468.robot.Lib.Control.TrapezoidalProfile;

public class Hatch implements Subsystem {
    private WPI_TalonSRX rotator_ = new WPI_TalonSRX(Constants.Hatch.rotator);
    private DoubleSolenoid popper_ = new DoubleSolenoid(Constants.Hatch.pop1, Constants.Hatch.pop2);
    private DigitalInput limit_ = new DigitalInput(Constants.Hatch.zeroer);
    
    public enum State {
        DISABLED,
        ZERO,
        PID,
        MP
    }

    private State state_ = State.DISABLED;
    private Value pop_ = Value.kReverse;
    private boolean zeroed_ = false;
    private double angle_ = -1;

    private double pErr_ = 0; // Previous error
    private MotionProfile motion_ = null;
    private double t_ = 0;

    public Hatch(){
        rotator_.configFactoryDefault();
        rotator_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        rotator_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        //rotator_.config_kD(0, 0);
    }

    public void setAngle(double theta){
        state_ = State.PID;
        angle_ = theta;
    }

    public void setAngleMotion(double theta){
        state_ = State.MP;
        angle_ = theta;
    }

    public void togglePop(){
        pop_ = (popper_.get() == Value.kForward) ? Value.kReverse : Value.kForward;
    }
    

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

    public double armMPFollower(double set, double angle, double vel, double acc){
        double err = set - angle;
        double o = (Constants.Hatch.kmP * err) +                                   // Power proportinal to error
                   (Constants.Hatch.kmD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Hatch.kF * Math.cos(angle * (Math.PI/ 180))) +       // Power to counteract gravity
                   (Constants.Hatch.kV * vel) +
                   (Constants.Hatch.kA * acc);
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return o;
    }

    public double angle(){
        return rotator_.getSelectedSensorPosition();
    }

    @Override public void start(){
        state_ = State.ZERO;
        pop_ = Value.kReverse;
    }

    @Override public void update(){
        if(!zeroed_) { state_ = State.ZERO; }
        if(popper_.get() != pop_){ popper_.set(pop_); }

        switch(state_){
            case ZERO:
                rotator_.set(ControlMode.PercentOutput, Constants.Hatch.zeroSpeed);
                if(limit_.get()) {
                    state_ = State.PID;
                    zeroed_ = true;
                }
                break;
            case DISABLED:
                rotator_.stopMotor();
                break;
            case PID:
                rotator_.set(ControlMode.PercentOutput,
                             armPDF(angle_, angle()));
                break;
            case MP:
                if(motion_ == null){
                    motion_ = new TrapezoidalProfile(angle_ - angle(),
                            Constants.Hatch.maxVel, Constants.Hatch.maxAcc).get();
                    t_ = Timer.getFPGATimestamp();
                }
                double t = Timer.getFPGATimestamp() - t_;
                if(!motion_.done(t)){
                    rotator_.set(ControlMode.PercentOutput,
                                 armMPFollower(motion_.x(t),
                                               angle(),
                                               motion_.v(t),
                                               motion_.a(t)));
                } else {
                    state_ = State.PID;
                    motion_ = null;
                }
                break;
        }
    }

    @Override public void stop(){
        rotator_.stopMotor();
        popper_.set(Value.kReverse);
    }

    @Override public void log(){
        SmartDashboard.putBoolean("IS ZEROING", state_ == State.ZERO);
        SmartDashboard.putBoolean("IS POPPED", popper_.get() == Value.kForward);
        SmartDashboard.putNumber("ANGLE", angle_);
        SmartDashboard.putNumber("Motor speed", rotator_.get());
    }
}