package frc.team4468.robot.Lib.Actions;

import java.util.ArrayDeque;
import java.util.Deque;

public class SeriesAction implements Action{
    private Action current_ = null;
    private Deque<Action> stack_ = new ArrayDeque<Action>();

    public SeriesAction(Action... acts){
        for (Action a : acts) { stack_.add(a); }
        current_ = null;
    }

    @Override public void done(){};
    @Override public void start(){};

    @Override
    public void update(){
        if(current_ != null){
            current_.update();
        } else {
            if(stack_.isEmpty()) { return; }
            current_ = stack_.pop();
            current_.update();
        }

        if(current_.isFinished()){
            current_.done();
            current_ = null;
        }
    }

    @Override
    public boolean isFinished(){ return stack_.isEmpty() && current_ == null; }
}