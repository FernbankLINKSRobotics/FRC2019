package frc.team4468.robot.Lib;

import java.util.function.Consumer;

public class SubsystemManager implements Subsystem {
    private Subsystem[] subs_;

    public SubsystemManager(Subsystem ... sub){ subs_ = sub; }

    private void map(Consumer<Subsystem> f){
        for (Subsystem s : subs_) { f.accept(s); }
    }

    @Override public void update() { map(s -> s.update());  }
    @Override public void start()  { map(s -> s.start());   }
    @Override public void stop()   { map(s -> s.stop());    }
    @Override public void log()    { map(s -> s.log());     }
}