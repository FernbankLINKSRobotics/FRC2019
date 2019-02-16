package frc.team4468.robot.Subs;

import frc.team4468.robot.Lib.Subsystem;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Robot;

public class SuperStructure implements Subsystem {
    // STATE VARIABLES
    public enum State {
        START,
        RETRACT,
        DEFAULT,
        MOVE_HATCH,
        MOVE_CARGO,
        DISABLED
    };

    private State state_ = State.DISABLED;
    private double hatchTarget;
    private double cargoTarget;
    private boolean hatchStarted = false;
    private boolean cargoStarted = false;

    // INPUTS OUTPUTS
    public void retractConfig(){ state_ = State.RETRACT; }
    public void defaultConfig(){ state_ = State.DEFAULT; }
    public void hatchAngle(double theta){
        state_ = State.MOVE_HATCH;
        hatchTarget = theta;
    }
    public void cargoAngle(double theta){
        state_ = State.MOVE_CARGO;
        cargoTarget = theta;
    }

    // HELPER FUNCTS
    private boolean hatchSafe() { return (Robot.hatch.angle() <= Constants.SuperStructure.hatchSafe); }
    private boolean cargoSafe() { return (Robot.cargo.angle() <= Constants.SuperStructure.cargoSafe); }

    // SUBSYSTEM IMPL
    @Override public void update(){
        switch(state_){
            case START:
                if(Robot.cargo.zeroed() && Robot.hatch.zeroed()){ // Arms are safe when ZEROED
                    state_ = State.DEFAULT;
                    break;
                }
                if(!cargoStarted){ Robot.cargo.start(); }
                if(cargoSafe()){
                    if(!hatchStarted){ Robot.hatch.start(); }
                }  
            break;

            case RETRACT:
                if(!hatchSafe()){ // move hatch out of the way
                    Robot.hatch.setAngle(Constants.SuperStructure.hatchRetract);
                    break;
                }
                if(!cargoSafe()){ // retracts 
                    Robot.cargo.setAngle(Constants.SuperStructure.cargoRetract);
                }
            break;

            case DEFAULT:
                if(!cargoSafe()){
                    Robot.cargo.setAngle(Constants.SuperStructure.cargoDefault);
                    break;
                }
                if(!hatchSafe()){
                    Robot.hatch.setAngle(Constants.SuperStructure.hatchDefault);
                }
            break;

            case MOVE_HATCH:
                if(hatchTarget > Constants.SuperStructure.hatchSafe && !cargoSafe()){
                    Robot.cargo.setAngle(Constants.SuperStructure.cargoSafe);
                    break;
                }
                Robot.hatch.setAngle(hatchTarget);
            break;

            case MOVE_CARGO:
                if(cargoTarget > Constants.SuperStructure.cargoSafe && !hatchSafe()){
                    Robot.hatch.setAngle(Constants.SuperStructure.hatchSafe);
                    break;
                }
                Robot.cargo.setAngle(cargoTarget);
            break;

            case DISABLED:
                Robot.cargo.stop();
                Robot.hatch.stop();
            break;
        }
    }

    @Override public void start(){
        state_ = State.START;
    }

    @Override public void stop(){
        state_ = State.DISABLED;
    }

    @Override public void log(){}
}