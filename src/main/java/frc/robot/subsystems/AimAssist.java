package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import java.util.function.Consumer;
import java.util.Map;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.Joystick;

public class AimAssist extends SubsystemBase {
    private MecanumDrive drive;
    private Map<String, NetworkTableEntry> limelightEntries;
    private Double limelightLastSeen;
    public Boolean clearToShoot = false;
    private Consumer<Double> shootMethod;

    // Config (These values may need to be changed)
    private NetworkTable limelightTable = (NetworkTableInstance.getDefault()).getTable("limelight");
    private Double noTargetDefaultPos = 0.5;
    private Double acceptableError = 5.0;
    private Double smallestPosCacheError = 20.0;
    private PIDController pid = new PIDController(0.35, 0.25, 0.1);
    private JoystickButton button = new JoystickButton(new Joystick(2), 2);
    public double maxSpeed = 0.4;

    // Constructor
    public AimAssist(MecanumDrive mecDrive, Consumer<Double> shoot) {
        limelightEntries.put("tx", limelightTable.getEntry("tx"));
        limelightEntries.put("ty", limelightTable.getEntry("ty"));
        limelightEntries.put("tv", limelightTable.getEntry("tv"));

        drive = mecDrive;
        shootMethod = shoot;
    }

    // Limelight Function(s)
    private boolean limelightHasTarget() {
        return (limelightEntries.get("tv").getDouble(0.0) == 1.0);
    }

    // Refresh & Run
    public void refresh() {
        if (limelightHasTarget()) {
            limelightLastSeen = limelightEntries.get("tx").getDouble(noTargetDefaultPos);
        }
    }

    // Shoot
    private void shoot() {
        shootMethod.accept(-1.0); // Distance not yet implemented
    }

    // Clamp Speed
    private double clampSpeed(double speed) {
        return Math.min(Math.max(speed, -maxSpeed), maxSpeed);
    }

    // Align
    private void align() {
        if (clearToShoot) {
            shoot();
            return;
        }

        double targetX;

        if (limelightHasTarget()) {
            targetX = limelightEntries.get("tx").getDouble(noTargetDefaultPos);
        } else if (limelightLastSeen != null && (limelightLastSeen > smallestPosCacheError || limelightLastSeen < -smallestPosCacheError)) {
            targetX = limelightLastSeen;
        } else {
            targetX = noTargetDefaultPos;
        }

        if (targetX < acceptableError && targetX > -acceptableError) {
            drive.driveCartesian(0.0, 0.0, 0.0);
            clearToShoot = true;
            shoot();
        } else {
            drive.driveCartesian(0.0, 0.0, clampSpeed(pid.calculate(targetX, 0.0)));
        }
    }

    // Run Teleop
    public void teleop() {
        if (button.get()) {
            align();
        } else {
            clearToShoot = false;
        }
    }

    // Run in Auto
    public void auto(Boolean goOrNoGo) {
        if (goOrNoGo) {
            align();
        } else {
            clearToShoot = false;
        }
    }
}