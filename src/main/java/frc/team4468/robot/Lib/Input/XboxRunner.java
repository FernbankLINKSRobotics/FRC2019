package frc.team4468.robot.Lib.Input;

public class XboxRunner extends HIDRunner {
    public XboxRunner(int ind){
        super(ind);
    }

    @Override public double getX(Hand hand) {
      if (hand.equals(Hand.kLeft)) {
        return getRawAxis(0);
      } else {
        return getRawAxis(4);
      }
    }
  
    @Override public double getY(Hand hand) {
      if (hand.equals(Hand.kLeft)) {
        return getRawAxis(1);
      } else {
        return getRawAxis(5);
      }
    }

    public double getTriggerAxis(Hand hand) {
        if (hand.equals(Hand.kLeft)) {
          return getRawAxis(2);
        } else {
          return getRawAxis(3);
        }
      }

    public void whenTriggerThreshold(Hand hand, double thres, Runnable f1){
      if(getTriggerAxis(hand) > thres){
        f1.run();
      }
    }

    public void whenTriggerThreshold(Hand hand, double thres, Runnable f1, Runnable f2){
      if(getTriggerAxis(hand) > thres){
        f1.run();
      } else {
        f2.run();
      }
    }
}