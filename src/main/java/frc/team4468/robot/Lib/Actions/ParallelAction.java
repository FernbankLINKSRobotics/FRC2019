package frc.team4468.robot.Lib.Actions;

import java.util.function.Consumer;

public class ParallelAction implements Action {
    private Action[] actions_;

    public ParallelAction(Action... acts){ actions_ = acts; }

    @Override
    public boolean isFinished(){
        boolean ret = true;
        for(Action a : actions_){ ret &= a.isFinished(); }
        return ret;
    }

    private void map(Consumer<Action> func){ 
        for(Action a: actions_){ func.accept(a); }
    }

    @Override public void start()  { map(a -> a.start());  }
    @Override public void update() { map(a -> a.update()); }
    @Override public void done()   { map(a -> a.done());   }
}