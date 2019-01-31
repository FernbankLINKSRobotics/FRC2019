package frc.team4468.robot.Lib.Control;

public class TriangularProfile implements MotionProfile {

    private double set_, x1; // Position variables 
    private double t1, tt; // time data
    private double Mv_, Ma_, vel_; // vel and acc
    private boolean trape;

    public TriangularProfile(double set, double vel, double acc){
        t1 = Math.sqrt(Math.abs(set)/Math.abs(acc));// Time to highest velocity
        set_ = set;                                 // setpoint
        Ma_ = acc;                                  // acceleration
        tt = 2 * t1;                                // time to finish
        Mv_ = acc * t1;                             // highest velocity
        x1 = .5 * acc * Math.pow(t1, 2);            // position at max velocity
        vel_ = vel;                                 // velocity
        trape = Math.abs(Mv_) > Math.abs(vel);
    }

    @Override public MotionProfile get(){
        if(trape){ return new TrapezoidalProfile(set_, vel_, Ma_); }
        else     { return this; }
    }

    @Override public double x(double t){
        if (t < t1) { return .5 * Ma_ * Math.pow(t, 2); }
        if (t < tt) { return x1 + ((Mv_ * (t-t1)) + (.5 * -Ma_ * Math.pow(t - t1, 2))); }
        else        { return set_; }
    }

    @Override public double v(double t){
        if (t < t1) { return Ma_ * t; }
        if (t < tt) { return Mv_ - (Ma_ * (t - t1)); }
        else        { return 0; }
    }

    @Override public double a(double t){
        if (t < t1) { return Ma_; }
        if (t < tt) { return -Ma_; }
        else        { return 0; }
    }

    @Override public boolean done(double t){ return t > tt; }
}

