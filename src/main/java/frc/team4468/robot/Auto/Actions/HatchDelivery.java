package frc.team4468.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.Action;

public class HatchDelivery implements Action {
    double t = 0;
    double dt = 0;

    public HatchDelivery(){}

    @Override public void update(){
        dt = Timer.getFPGATimestamp() - t;
        if(dt > 0.1){ // PLACEHOLDER
            Robot.hatch.setPop(true);
        }
    }
    @Override public boolean isFinished(){ return dt > 0.5; } // PLACEHOLDER FOR NOW

    @Override public void start() {
        Robot.hatch.setClamp(false);
        t = Timer.getFPGATimestamp();
    }

    @Override public void done() {
        Robot.hatch.setPop(true);
        Robot.hatch.setAngle(180);
    }
}