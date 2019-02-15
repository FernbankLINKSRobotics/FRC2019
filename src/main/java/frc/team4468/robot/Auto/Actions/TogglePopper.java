package frc.team4468.robot.Auto.Actions;

import frc.team4468.robot.Robot;
import frc.team4468.robot.Lib.Actions.OneTimeAction;

public class TogglePopper extends OneTimeAction {
    public TogglePopper(){}

    @Override public void run(){ Robot.hatch.togglePop(); }
}