package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.OneTimeAction;

public class Shift extends OneTimeAction {
    private boolean high_ = false;

    public Shift(boolean high){ high_ = high; }

    @Override public void run(){ Robot.drive.setGear(high_); }
}