package frc.team4468.robot.Lib.Input;

import edu.wpi.first.wpilibj.GenericHID;

public abstract class HIDRunner extends GenericHID {
    public HIDRunner(int ind){
        super(ind);
    }

    public void whenPressed(int ind, Runnable f){
        if(getRawButtonPressed(ind)){ f.run(); }
    }

    public void whenReleased(int ind, Runnable f){
        if(getRawButtonReleased(ind)){ f.run(); }
    }

    public void whenGreater(int ind, double thres, Runnable f){
        if(getRawAxis(ind) > thres){ f.run(); }
    }

    public void whenLesser(int ind, double thres, Runnable f){
        if(getRawAxis(ind) < thres){ f.run(); }
    }
}