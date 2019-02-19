package frc.team4468.robot.Subs;

import frc.team4468.robot.Lib.Subsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import frc.team4468.robot.Constants;
import frc.team4468.robot.Robot;

public class SuperStructure implements Subsystem {
    // HARDWARE
    Compressor comp_ = new Compressor();
    PowerDistributionPanel pdp_ = new PowerDistributionPanel();

    // STATE VARIABLES
    public enum Manipulators {
        START,
        RETRACT,
        DEFAULT,
        MOVE_HATCH,
        MOVE_CARGO,
        DISABLED
    };

    private Manipulators manipulators_ = Manipulators.DISABLED;
    private boolean hatchStarted = false;
    private boolean cargoStarted = false;
    private double hatchTarget;
    private double cargoTarget;

    // PUBLIC INPUTS OUTPUTS
    public void retract(){ manipulators_ = Manipulators.RETRACT; }
    public void disable(){ manipulators_ = Manipulators.DEFAULT; }
    public void setHatch(double theta){
        manipulators_ = Manipulators.MOVE_HATCH;
        hatchTarget = theta;
    }
    public void setCargo(double theta){
        manipulators_ = Manipulators.MOVE_CARGO;
        cargoTarget = theta;
    }

    // PRIVATE HELPER FUNCTS
    private boolean hatchSafe() { return (Robot.hatch.angle() <= Constants.SuperStructure.hatchSafe); }
    private boolean cargoSafe() { return (Robot.cargo.angle() <= Constants.SuperStructure.cargoSafe); }
    private void manipulatorState(Manipulators manip){
        switch(manip){
            case START:
                if(Robot.cargo.zeroed() && Robot.hatch.zeroed()){ // Arms are safe when ZEROED
                    manipulators_ = Manipulators.DEFAULT;
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
                //Robot.cargo.stop();
                //Robot.hatch.stop();
            break;
        }
    }

    // SUBSYSTEM IMPL
    @Override public void update(){
        manipulatorState(manipulators_);
        if(RobotController.isBrownedOut()){
            Robot.drive.setModifier(Constants.SuperStructure.driveModifier);
            comp_.stop();
        } else if(pdp_.getTotalPower() > Constants.SuperStructure.warningPower){
            comp_.stop();  
        } else {
            Robot.drive.setModifier(1);
            comp_.start();
        }
    }

    @Override public void start(){
        manipulators_ = Manipulators.DISABLED;
        comp_.clearAllPCMStickyFaults();
        pdp_.clearStickyFaults();
    }

    @Override public void stop(){
        manipulators_ = Manipulators.DISABLED;
    }

    @Override public void log(){}
}