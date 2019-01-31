package frc.team4468.robot.Lib.Control;

public class TrapezoidalProfile implements MotionProfile {

    private double set_, Mv_, Ma_;
    private boolean tri;
    private double t1, t2, tt, tv;
    private double x1, x2;

    public TrapezoidalProfile(double set, double vel, double acc){
        if(set < 0){
            vel = -vel;
            acc = -acc;
        }
        tri = (Math.abs(vel) > (Math.abs(acc) * Math.sqrt(set/acc)));
        set_ = set;                       // Final position
        Mv_ = vel;                        // Max velocity
        Ma_ = acc;                        // Mac acceleration
        t1 = vel/acc;                     // Time to max velocity
        x1 =  .5 * acc * Math.pow(t1, 2); // Distance when v reaches max velocity
        tv = (set - (2*x1)) / Mv_;        // Time at max velocity
        t2 = t1 + tv;                     // Time to start deceleration
        x2 = set - x1;                    // Position to start deceleration
        tt = t2 + t1;                     // Total time
    }

    @Override public MotionProfile get(){
        if(tri){ return new TriangularProfile(set_, Mv_, Ma_); }
        else   { return this; }
    }

    @Override public double x(double t){
        if (t < t1) { return .5 * Ma_ * Math.pow(t, 2); }
        if (t < t2) { return x1 + (Mv_ * (t- t1)); }
        if (t < tt) { return x2 + ((Mv_ * (t-t2)) + (.5 * -Ma_ * Math.pow(t-t2, 2))); }
        else        { return set_; }
    }
    @Override public double v(double t){
        if (t < t1) { return Ma_ * t; }
        if (t < t2) { return Mv_; }
        if (t < tt) { return Mv_ - (Ma_ * (t - t2)); }
        else        { return 0; }
    }
    @Override public double a(double t){
        if (t < t1) { return Ma_;}
        if (t < t2) { return 0; }
        if (t < tt) { return -Ma_; }
        else        { return 0; }
    }

    @Override public boolean done(double t){ return t > tt; }
}