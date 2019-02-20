package frc.team4468.robot.Lib.Input;

public class JoystickRunner extends HIDRunner {
    public JoystickRunner(int ind){
        super(ind);
    }

    public boolean getTrigger(){
        return getRawButton(1);
    }

    public boolean getTriggerPressed(){
        return getRawButtonPressed(1);
    }

    public boolean getTriggerReleased(){
        return getRawButtonReleased(1);
    }

    public void whenTriggerPressed(Runnable f){
        if(getTriggerPressed()){ f.run(); }
    }

    public void whenTriggerReleased(Runnable f){
        if(getTriggerReleased()){ f.run(); }
    }

    @Override public double getX(Hand hand) {
        return getRawAxis(0);
    }
  
    @Override public double getY(Hand hand) {
        return getRawAxis(1);
    }
}