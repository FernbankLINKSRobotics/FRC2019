package frc.team4468.robot.Auto.Macros;

import frc.team4468.robot.Auto.Actions.ExampleAction;
import frc.team4468.robot.Lib.Actions.Macro;

public class ExampleMacro extends Macro {
    public ExampleMacro(){}

    public void routine(){
        addAction(new ExampleAction());
    }
}