package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.Action;

public class Pop implements Action {
    public Pop(){}

    @Override public void update() {}
    @Override public boolean isFinished(){ return true; }

    @Override public void start() {
        Robot.hatch.togglePop();
    }

    @Override public void done() {
        Robot.hatch.togglePop();
    }
}