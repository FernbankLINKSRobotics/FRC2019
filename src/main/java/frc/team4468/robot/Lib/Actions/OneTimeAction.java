package frc.team4468.robot.Lib.Actions;

public abstract class OneTimeAction implements Action {
    @Override public boolean isFinished(){ return true; }
    @Override public void start (){ run(); }
    @Override public void update(){}
    @Override public void done  (){}

    public abstract void run();
}