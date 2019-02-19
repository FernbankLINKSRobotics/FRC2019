package frc.team4468.robot.Subs;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;
import frc.team4468.robot.Lib.Control.MotionProfile;
import frc.team4468.robot.Lib.Control.TrapezoidalProfile;

public class Cargo implements Subsystem {
    // HARDWARE
    private WPI_VictorSPX intake_ =  new WPI_VictorSPX(Constants.Cargo.intake);
    private WPI_TalonSRX rotator_ = new WPI_TalonSRX(Constants.Cargo.rotator);
    private DigitalInput limit_ = new DigitalInput(Constants.Cargo.zeroer);
    

    // STATE VARIABLES
    public enum State {
        DISABLED,
        ZERO,
        PID,
        MP
    }

    private State state_ = State.DISABLED;
    private MotionProfile motion_ = null;
    private boolean zeroed_ = false;
    private double angle_ = 90;
    private double speed_ = 0;
    private double pErr_ = 0;
    private double t_ = 0;

    // CONSTUCTOR
    public Cargo() {
        rotator_.configFactoryDefault();
        rotator_.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rotator_.enableVoltageCompensation(true);
    }

    // PUBLIC INPUT OUTPUT
    public void setAngle(double theta){
        state_ = State.PID;
        angle_ = theta;
        System.out.println("angle: " + angle_);
    }

    public void setAngleMotion(double theta){
        state_ = State.MP;
        angle_ = theta;
    }

    public void setIntake(double speed){ speed_ = speed; }

    public double angle(){
        return ticksToAngle(rotator_.getSelectedSensorPosition(0));
    }

    public boolean zeroed(){ return zeroed_; }

    // PRIVATE HELPER FUNCTS
    private double armPDF(double set, double angle){
        double err = set - angle;
        double o = (Constants.Cargo.kP * err) +                                   // Power proportinal to error
                   (Constants.Cargo.kD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Cargo.kF * Math.sin(angle * (Math.PI/ 180)));       // Power to counteract gravity
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return -o;
    }

    private double armMPFollower(double set, double angle, double vel, double acc){
        double err = set - angle;
        double o = (Constants.Cargo.kmP * err) +                                   // Power proportinal to error
                   (Constants.Cargo.kmD * ((err - pErr_) / Constants.System.dt)) + // Power related to the derivative
                   (Constants.Cargo.kF * Math.sin(angle * (Math.PI/ 180))) +       // Power to counteract gravity
                   (Constants.Cargo.kV * vel) +                                    // Feed forward for velocity
                   (Constants.Cargo.kA * acc);                                     // Feed forward for acceleration
        pErr_ = err;
        o = (o >  1) ? 1 :     // clamps the range to -1 to 1
            (o < -1) ? -1 : o;
        return -o;
    }

    private int angleToTicks(double angle){
        return (int)(angle * (4096/(360 * Constants.Cargo.armRatio)));
    }

    private double ticksToAngle(int ticks){
        return ticks * ((360 * Constants.Cargo.armRatio)/4096);
    }

    
    // SUBSYSTEM IMPL
    @Override public void start(){
        state_ = State.ZERO;
        angle_ = 90;
    }

    @Override public void update(){
        if(!zeroed_) { state_ = State.ZERO; }
        //System.out.println("Zero: " + !limit_.get());
        //System.out.println("Angle: " + angle());
        //System.out.println("Error: " + (angle_ - angle()));
        //System.out.println("Ticks: " + rotator_.getSelectedSensorPosition(0));
        //System.out.println("Target: " + angle_);
        
        switch(state_){
            case ZERO:
                rotator_.set(ControlMode.PercentOutput, Constants.Cargo.zeroSpeed);
                if(!limit_.get()) {
                    rotator_.setSelectedSensorPosition(angleToTicks(Constants.Cargo.zeroAngle), 0, 10);
                    state_ = State.PID;
                    zeroed_ = true;
                }
                break;
            case DISABLED:
                rotator_.stopMotor();
                break;
            case PID:
                rotator_.set(ControlMode.PercentOutput, armPDF(angle_, angle()));
                break;
            case MP:
                if(motion_ == null){
                    motion_ = new TrapezoidalProfile(angle_ - angle(),
                            Constants.Cargo.maxVel, Constants.Cargo.maxAcc).get();
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
        
        intake_.set(ControlMode.PercentOutput, speed_);
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