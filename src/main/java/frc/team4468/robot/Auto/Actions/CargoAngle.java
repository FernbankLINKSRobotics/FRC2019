package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.OneTimeAction;

public class CargoAngle extends OneTimeAction {
    private double angle_;
    private boolean profile_ = false; // Run MP or not

    public CargoAngle(double angle){
        angle_ = angle;
    }

    public CargoAngle(double angle, boolean profile){
        angle_ = angle;
        profile_ = profile;
    }

    @Override public void run(){
        if(!profile_){
            Robot.cargo.setAngle(angle_);
        } else {
            Robot.cargo.setAngleMotion(angle_);
        }
    }
}