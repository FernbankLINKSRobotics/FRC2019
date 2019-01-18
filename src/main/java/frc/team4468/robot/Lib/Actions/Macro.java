package frc.team4468.robot.Lib.Actions;

public abstract class Macro extends OneTimeAction{
    private final long period_ = 20;

    protected abstract void routine() throws Exception;

    public void run(){
        try {
            routine();
        } catch(Exception e){
            System.out.println("MACRO ENDED");
            return;
        }
    }

    public void addAction(Action a){
        a.start();

        while(!a.isFinished()){
            a.update();
            try {
                Thread.sleep(period_);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
        a.done();
    }
}