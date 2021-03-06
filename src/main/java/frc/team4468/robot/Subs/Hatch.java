package frc.team4468.robot.Subs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;
import frc.team4468.robot.Lib.Control.MotionProfile;
import frc.team4468.robot.Lib.Control.TrapezoidalProfile;

public class Hatch implements Subsystem {
    // HARDWARE
    private WPI_TalonSRX rotator_ = new WPI_TalonSRX(Constants.Hatch.rotator);
    private DoubleSolenoid clamp_ = new DoubleSolenoid(Constants.Hatch.clamp1, Constants.Hatch.clamp2);
    private DoubleSolenoid popper_ = new DoubleSolenoid(Constants.Hatch.pop1, Constants.Hatch.pop2);
    
    //private DigitalInput zero_ = new DigitalInput(Constants.Hatch.zeroer);
    //private DigitalInput grab_ = new DigitalInput(Constants.Hatch.grab);
    
    // STATE VARIABLES
    public enum State {
        DISABLED,
        ZERO,
        PID,
        MP
    }

    private State state_ = State.DISABLED;
    private Value open_ = Value.kReverse;
    private Value pop_ = Value.kReverse;
    private boolean zeroed_ = false;
    private double angle_ = 180;

    private double pErr_ = 0; // Previous error
    private double tErr_ = 0; // Integral of error
    private MotionProfile motion_ = null;
    private double t_ = 0;

    // CONSTRUCTOR
    public Hatch(){
        rotator_.configFactoryDefault();
        rotator_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        rotator_.enableVoltageCompensation(true);
        rotator_.setInverted(true);
    }

    // PUBLIC INPUT OUTPUT
    public void setAngle(double theta){
        state_ = State.PID;
        angle_ = theta;
        tErr_ = 0; // resets integrand 
    }

    public void setAngleMotion(double theta){
        state_ = State.MP;
        angle_ = theta;
        tErr_ = 0;
    }

    public void togglePop(){
        pop_ = (popper_.get() == Value.kForward) ? Value.kReverse : Value.kForward;
    }

    public void toggleClamp(){
        open_ = (clamp_.get() == Value.kForward) ? Value.kReverse : Value.kForward;
    }

    public void setPop(boolean b){
        pop_ = (b) ? Value.kForward : Value.kReverse;
    }

    public void setClamp(boolean b){
        open_ = (b) ? Value.kForward : Value.kReverse;
    }

    public boolean isPoped(){
        return popper_.get() == Value.kForward;
    }

    public double angle(){
        return ticksToAngle(rotator_.getSelectedSensorPosition(0));
    }

    // PRIVATE HELPER FUNCTS
    private double armPDF(double set, double angle){
        double err = set - angle;
        tErr_ += err;
        double o = (Constants.Hatch.kP * err) +                                   // Power proportinal to error
                   (Constants.Hatch.kI * tErr_ * Constants.System.dt) +           // Power related to the integral
                   (Constants.Hatch.kD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Hatch.kF * Math.sin(angle * (Math.PI/ 180)));       // Power to counteract gravity
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return -o;
    }

    private double armMPFollower(double set, double angle, double vel, double acc){
        double err = set - angle;
        tErr_ += err;
        double o = (Constants.Hatch.kmP * err) +                                   // Power proportinal to error
                   (Constants.Hatch.kmI * tErr_ * Constants.System.dt) +            // Power related to the integral
                   (Constants.Hatch.kmD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Hatch.kF * Math.sin(angle * (Math.PI/ 180))) +       // Power to counteract gravity
                   (Constants.Hatch.kV * vel) +
                   (Constants.Hatch.kA * acc);
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return -o;
    }

    
    private int angleToTicks(double angle){
        return (int)(angle * (4096/(360 * Constants.Hatch.armRatio)));
    }

    private double ticksToAngle(int ticks){
        return ticks * ((360 * Constants.Hatch.armRatio)/4096);
    }
    

    // SUBSYSTEM IMPL
    @Override public void start(){
        state_ = State.ZERO;
        pop_ = Value.kReverse;
        open_ = Value.kReverse;
        angle_ = 160;
    }

    @Override public void update(){
        clamp_.set(open_);
        popper_.set(pop_);

        switch(state_){
            case ZERO:
                rotator_.setSelectedSensorPosition(angleToTicks(Constants.Hatch.zeroAngle), 0, 10);
                state_ = State.DISABLED;
                zeroed_ = true;
                break;
            case DISABLED:
                rotator_.stopMotor();
                break;
            case PID:
                double out = armPDF(angle_, angle());
                System.out.println("Power: " + out);
                rotator_.set(ControlMode.PercentOutput,
                             out);
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
                    tErr_ = 0;
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
        SmartDashboard.putBoolean("Hatch Zero", zeroed_);
        SmartDashboard.putNumber("Hatch Angle", angle());
        SmartDashboard.putNumber("Hatch Power", rotator_.get());
    }
}