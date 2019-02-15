package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.OneTimeAction;

public class HatchAngle extends OneTimeAction {
    private double angle_;
    private boolean profile_ = false; // Run MP or not

    public HatchAngle(double angle){
        angle_ = angle;
    }

    public HatchAngle(double angle, boolean profile){
        angle_ = angle;
        profile_ = profile;
    }

    @Override public void run(){
        if(!profile_){
            Robot.hatch.setAngle(angle_);
        } else {
            Robot.hatch.setAngleMotion(angle_);
        }
    }
}