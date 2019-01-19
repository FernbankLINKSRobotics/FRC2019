package frc.team4468.robot.Lib.Actions;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

/*
This class runs either Macros (series of actions) or individual actions.
It uses a fixed thread pool to try to minimize Thread start up time and handoffs.
We submit Actions (Macros extend Actions) and get a future that runs the Action
and kills itself once done. There is a HashMap<String, Future<?>> to be able to
address the futures, this allows us to kill Macros early. The HashMap also means 
that we dont get multiple Actions that compound on each other.
*/
public class MacroExecutor {
    private ExecutorService exec_;
    private HashMap<String, Future<?>> stack_ = new HashMap<>();
    private boolean terminate_ = false;

    public MacroExecutor(int num){
        exec_ = Executors.newFixedThreadPool(num);
    }

    public void execute(String s, Action a){
        // This prevents 2 of the same action to be run
        if(stack_.containsKey(s)){
            System.out.println("ALREADY RUNNING MACRO: " + s);
            return;
        }

        Future<?> f = exec_.submit(() -> {
                if(a != null){ 
                    a.start(); // Starts Action
                    while(!a.isFinished() && !terminate_){
                        a.start(); // Iterates until done
                    }
                    a.done(); // Ends it
                }

                stop(s); // Frees the future slot
            }
        );
        stack_.put(s, f); // Records the futues
    }

    public void stop(String s){
        // Handles if there is no future
        if(!stack_.containsKey(s)){
            System.out.println("NO MACRO");
            return;
        }

        Future<?> f = stack_.remove(s); // removes it from the queue
        if(f == null){ return;} // ends if nothing there
        f.cancel(true); // kills the future
        System.out.println("KILLED MACRO: " + s);
    }

    // kills the threadpool
    public void end(){
        terminate_ = true; // stops Action from updating infinitely
        stack_.forEach((s, f) -> f.cancel(true)); // kills each future 
        exec_.shutdownNow(); // kills threads
        System.out.println("KILL ALL CURRENT MACROS");
    }
}