package frc.team4468.robot;

public static class Constants {

    public class System {
        public static final double dt = 0.02;
        public static final int CANTimeout = 100; //use for on the fly updates
    }

    public class Input {
        public static final int driver = 0;
        public static final int operator = 0;
    }

    public class Drive {
        // Motors
        public static final int leftMaster = 0;
        public static final int leftSlave1 = 1;
        public static final int leftSlave2 = 2;
        public static final int rightMaster = 3;
        public static final int rightSlave1 = 4;
        public static final int rightSlave2 = 5;
        // PIDs
        public static final double posP = 0;
        public static final double posI = 0;
        public static final double posD = 0;
        public static final double posF = 0;
        public static final double velP = 0;
        public static final double velI = 0;
        public static final double velD = 0;
        public static final double velF = 0;
        // Misc
        public static final double rampRate = 0.01;
        public static final int shift1 = 2;
        public static final int shift2 = 3;
    }

    public class Hatch {
        public static final int rotator = 6; // Motor for rotating
        public static final int pop1 = 0; // solenoid ports
        public static final int pop2 = 1;
        public static final int zeroer = 0;
        public static final double zeroSpeed = 0.2;
        public static final double armRatio = 1/1;

        // PDFVA
        public static final double kP = 0;
        public static final double kD = 0;
        public static final double kmP = 0;
        public static final double kmD = 0;
        public static final double kF = 0;
        public static final double kV = 0;
        public static final double kA = 0;
        // Motion Profile
        public static final double maxVel = 5;
        public static final double maxAcc = 5;
    }

    public class Cargo {
        public static final int intake = 7;
        public static final int rotator = 8;
        public static final int zeroer = 0;
        public static final double zeroSpeed = 0.2;
        public static final double armRatio = 24/84;
        // PDFVA
        public static final double kP = 0;
        public static final double kD = 0;
        public static final double kmP = 0;
        public static final double kmD = 0;
        public static final double kF = 0;
        public static final double kV = 0;
        public static final double kA = 0;
        // Motion Profile
        public static final double maxVel = 5;
        public static final double maxAcc = 5;
    }

    public class Fourbar {
        public static final int leftLift = 9;
        public static final int rightLift = 10;
    }

    public class SuperStructure {
        public static final double cargoSafe = 120;
        public static final double hatchSafe = 200;
        public static final double cargoDefault = 90;
        public static final double hatchDefault = 180;
        public static final double cargoRetract = 200;
        public static final double hatchRetract = 220;
    }
}