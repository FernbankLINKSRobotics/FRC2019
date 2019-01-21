package frc.team4468.robot.Subs;

import frc.team4468.robot.Constants;
import frc.team4468.robot.Lib.Subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive implements Subsystem {
    private WPI_TalonSRX leftMaster = new WPI_TalonSRX(Constants.Drive.leftMaster);
    private WPI_VictorSPX leftSlave1 = new WPI_VictorSPX(Constants.Drive.leftSlave1);
    private WPI_VictorSPX leftSlave2 = new WPI_VictorSPX(Constants.Drive.leftSlave2);
    private WPI_TalonSRX rightMaster = new WPI_TalonSRX(Constants.Drive.rightMaster);
    private WPI_VictorSPX rightSlave1 = new WPI_VictorSPX(Constants.Drive.rightSlave1);
    private WPI_VictorSPX rightSlave2 = new WPI_VictorSPX(Constants.Drive.rightSlave2);

    private DifferentialDrive drive_ = new DifferentialDrive(
        new SpeedControllerGroup(
            leftMaster,
            leftSlave1,
            leftSlave2),
        new SpeedControllerGroup(
            rightMaster,
            rightSlave1,
            rightSlave2)
    );

    private static double leftSpeed_ = 0;
    private static double rightSpeed_ = 0;

    public Drive(){
        leftMaster.configFactoryDefault();
        leftSlave1.configFactoryDefault();
        leftSlave2.configFactoryDefault();
        rightMaster.configFactoryDefault();
        rightSlave1.configFactoryDefault();
        rightSlave2.configFactoryDefault();

        leftSlave1.follow(leftMaster);
        leftSlave2.follow(leftMaster);
        rightSlave1.follow(rightMaster);
        rightSlave2.follow(rightMaster);

        leftMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
        rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftMaster.setInverted(true);
        rightMaster.setInverted(false);
        leftSlave1.setInverted(InvertType.FollowMaster);
        leftSlave2.setInverted(InvertType.FollowMaster);
        rightSlave1.setInverted(InvertType.FollowMaster);
        rightSlave2.setInverted(InvertType.FollowMaster);
    }

    public static void setTank(double left, double right){
        leftSpeed_ = left;
        rightSpeed_ = right;
    }

    @Override public void update(){
        drive_.tankDrive(leftSpeed_, rightSpeed_);
    }

    @Override public void start(){}
    @Override public void stop(){
        drive_.stopMotor();
    }
    @Override public void log(){}
}