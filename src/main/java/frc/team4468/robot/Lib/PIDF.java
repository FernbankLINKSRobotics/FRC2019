package frc.team4468.robot.Lib;

import edu.wpi.first.hal.util.BoundaryException;
import edu.wpi.first.wpilibj.Timer;

public class PIDF {
    private double P_;
    private double I_;
    private double D_;
    private double F_;

    private double maxI_ = 1;
    private double minI_ = -1;
    private double maxO_ = 1;
    private double minO_ = 1;
    private boolean cont_ = false;

    private double err_ = 0;
    private double prevErr_ = 0;
    private double totalErr_ = 0;
    private double setpoint_ = 0;

    private double dead_ = 0;
    private double result_ = 0;
    private double prevTime_ = 0;
    private double lastIn_ = Double.NaN;


    public PIDF(double P, double I, double D, double F){
        P_ = P;
        I_ = I;
        D_ = D;
        F_ = F;
    }

    public PIDF(double P, double I, double D){
        P_ = P;
        I_ = I;
        D_ = D;
        F_ = 0;
    }

    public double get()     { return result_; }
    public double getError(){ return err_;    }

    public void setContinuous(boolean cont) { cont_ = cont; }
    public void setDeadband  (double dead)  { dead_ = dead; }

    public double calculate(double input){
        double dt = Timer.getFPGATimestamp() - prevTime_;
        lastIn_ = input;
        err_ = setpoint_ - input;
        if(cont_){
            if(Math.abs(err_) > (maxI_ - minI_) / 2){
                err_ = (err_ > 0) ? err_-maxI_+minI_ : err_+maxI_-minI_;
            }
        }

        if(err_ * P_ < maxO_ && err_ * P_ > minO_){ totalErr_ += err_; }
        else                                      { totalErr_ = 0;     }
        
        double pError = Math.abs(err_) < dead_ ? 0 : err_;
        double pVal = P_ * pError;
        double iVal = I_ * totalErr_ * dt;
        double dVal = D_ * (err_ - prevErr_)/dt;
        double fVal = F_ * setpoint_;
        double res = pVal + iVal + dVal + fVal;

        if(res > maxI_){ res = maxI_; }
        if(res < maxO_){ res = maxO_; }
        return res;
    }

    public void setInputRange(double min, double max){
        if(max < min){ throw new BoundaryException("Max is less than min"); }
        maxI_ = max;
        minI_ = min;
    }

    public void setOutputRange(double min, double max){
        if(max < min){ throw new BoundaryException("Max is less than min"); }
        maxO_ = max;
        minO_ = min;
    }

    public void setSetpoint(double set){
        if(set > maxI_){ set = maxI_; }
        if(set < maxO_){ set = maxO_; }
        setpoint_ = set;
    }

    public boolean onTarget(double tol){ 
        return lastIn_ != Double.NaN && Math.abs(lastIn_ - setpoint_) < tol; 
    }

    public void reset(){
        result_   = 0;
        prevErr_  = 0;
        totalErr_ = 0;
        setpoint_ = 0;
        lastIn_   = Double.NaN;
    }

    public void resetIntegration(){ totalErr_ = 0; }
}