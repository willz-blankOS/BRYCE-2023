package frc.robot.Auto;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Util.DriveController;
import frc.robot.subsystems.DriveSubsystem;

public class AutoBalance extends CommandBase {
    private DriveSubsystem drive;
    private DriveController driveController;
    private PIDController controller;
    private double Output, gain = 1;
    private boolean isReversed;
    private int state = 0;

    public AutoBalance(DriveSubsystem drive, DriveController driveController, boolean isReversed) {
        this.drive = drive;
        this.driveController = driveController;
        this.isReversed = isReversed;
        this.state = 0;

        addRequirements(drive);
    }

    public AutoBalance(DriveSubsystem drive, DriveController driveController, boolean isReversed, int state) {
        this.drive = drive;
        this.driveController = driveController;
        this.isReversed = isReversed;
        this.state = state;
        
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        drive.setBrakeMode(true);
        controller = new PIDController(0.007, 0, 0);
        controller.setTolerance(0.7);

        if(isReversed) {
            gain = -1;
        }else{
            gain = 1;
        }
    }

    @Override
    public void execute() {
        if(state == 0){
            driveController.drive(0, gain * 0.2, 0, false);
        }
        
        if(state > 0){
            
            Output = controller.calculate(drive.getRoll(), 0);
                Output = Math.signum(Output) * 0.15 * gain;
                driveController.drive(0,  -controller.calculate(drive.getRoll(), 0), 0, false);
            
            //drive.stop();
        }

        if(Math.abs(drive.getRoll()) > 14){
            state++;
        }
    }

    @Override
    public void end(boolean interrupted) {
        drive.baselock();
    }

    @Override
    public boolean isFinished() {
        if(controller.atSetpoint()){
            return true;
        }else{
            return false;
        }
    }
    
}
