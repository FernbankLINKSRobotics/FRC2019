package frc.team4468.robot.Subs;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import frc.team4468.robot.Lib.Subsystem;

public class Vision implements Subsystem {
    private static NetworkTableInstance inst = NetworkTableInstance.getDefault();
    private static NetworkTable table;

    double x_, y_, angle_ = 0;
    boolean online_, track_, target_ = false;

    public Vision(){
        table = inst.getTable("vision_table");
        table.getEntry("stop").setBoolean(false);
    }
    
    public double getX(){ return x_; }
    public double getY(){ return y_; }
    public double getAngle() { return angle_; }
    public boolean isOnline() { return online_; }
    public boolean isTracking() { return track_; }
    public void setTarget(boolean b) { target_ = b; }

    @Override public void start(){}
    @Override public void update(){
        x_ = table.getEntry("X").getDouble(0);
        y_ = table.getEntry("Y").getDouble(0);
        angle_ = table.getEntry("Angle").getDouble(0);
        track_ = table.getEntry("Tracking").getBoolean(false);
        table.getEntry("Target").setBoolean(target_);
    }
    @Override public void stop(){}
    @Override public void log(){
        SmartDashboard.putBoolean("Vision Online", online_);
        SmartDashboard.putBoolean("Tracking", track_);
        SmartDashboard.putNumber("Angle", angle_);
        SmartDashboard.putNumber("X", x_);
        SmartDashboard.putNumber("Y", y_);
    }
}