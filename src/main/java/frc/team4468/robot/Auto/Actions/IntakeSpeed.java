package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.OneTimeAction;

public class IntakeSpeed extends OneTimeAction {
    private double speed_;

    public IntakeSpeed(double s){ speed_ = s; }

    @Override public void run(){
        Robot.cargo.setIntake(speed_);
    }
}