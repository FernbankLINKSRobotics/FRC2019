package frc.team4468.robot.Lib.Control;

public interface MotionProfile {
    public abstract MotionProfile get();
    public abstract boolean done(double t);

    public abstract double x(double t);
    public abstract double v(double t);
    public abstract double a(double t);
}