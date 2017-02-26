package edu.wpi.moderndash.views;

import edu.wpi.moderndash.concurrent.DaemonThread;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

import lombok.NonNull;

import static edu.wpi.moderndash.views.DriveBaseView.DriveType.SKID_STEER;

/**
 * A view for a robots drive base.
 */
@DefaultProperty("children")
public class DriveBaseView extends Region {

    /**
     * An enum representing the most common types of drive bases that FRC teams will use.
     */
    public enum DriveType {
        /**
         * Skid-steer drive. Each side of the drive train moves as a unit, and rotates by driving each side at
         * different speeds or directions. This is the most common and therefore the default.
         */
        SKID_STEER,
        /**
         * Mecanum drive. Four mecanum wheels, one in each corner, drive the robot with
         * three degrees of freedom.
         */
        MECANUM,
        /**
         * Similar to mecanum, but with each corner having omni wheels mounted at a 45-degree angle to
         * that corner.
         */
        OMNI,
        /**
         * A subset of {@link #OMNI omni drive}, but with only three sides on the robot with wheels at 120 degrees
         * to each other.
         */
        KIWI,
        /**
         * A complicated holonomic drive system that's basically a four-wheel skid-steer, but where each wheel can
         * rotate about its own vertical axis independently of the chassis.
         */
        SWERVE,
        /**
         * Skid steer with all omni wheels, plus an extra omni wheel at the center of rotation perpendicular
         * to the rest of the wheels. This allows an omni-drive robot to move in all directions like a mecanum
         * drive, but without the loss of force.
         */
        H,
        /**
         * No wheels, only tank treads. One per side.
         */
        TANK_TREAD
    }

    /**
     * The number of wheels on the drive base. Defaults to 4.
     */
    private IntegerProperty numWheels = new SimpleIntegerProperty(this, "numWheels", 4);

    /**
     * The type of the drive the robot uses. Defaults to {@link DriveType#SKID_STEER skid steer}.
     */
    private ObjectProperty<DriveType> driveType = new SimpleObjectProperty<>(this, "driveType", SKID_STEER);

    /**
     * The angle of the robot. This is usually mapped to a gyro for accurate measurements.
     */
    private DoubleProperty angle = new SimpleDoubleProperty(this, "angle", 0);

    /**
     * Creates a new drive base view with 4-wheel skid steer and no angle.
     */
    public DriveBaseView() {
        this(4, SKID_STEER, 0);
    }

    /**
     * Creates a drive base view with the given number of wheels, the given drive type, and an initial angle.
     *
     * @param numWheels    the initial number of wheels on the view
     * @param driveType    the initial type of drive (eg mecanum, skid steer)
     * @param initialAngle the initial angle of the view
     */
    public DriveBaseView(int numWheels, @NonNull DriveType driveType, double initialAngle) {
        getStylesheets().add("/edu/wpi/moderndash/css/drive-base.css");
        getStyleClass().add("drive-base-drive");

        setNumWheels(numWheels);
        setDriveType(driveType);
        setAngle(initialAngle);

        rotateProperty().bind(angleProperty());

        // Add random rotation for testing
        Executors.newSingleThreadScheduledExecutor(DaemonThread::new).scheduleAtFixedRate(() -> {
//            Platform.runLater(() -> setAngle(getAngle() + Math.random() * 5 - 2.5));
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    // Beans getters and setters

    public int getNumWheels() {
        return numWheels.get();
    }

    public IntegerProperty numWheelsProperty() {
        return numWheels;
    }

    public void setNumWheels(int numWheels) {
        this.numWheels.set(numWheels);
    }

    public DriveType getDriveType() {
        return driveType.get();
    }

    public ObjectProperty<DriveType> driveTypeProperty() {
        return driveType;
    }

    public void setDriveType(@NonNull DriveType driveType) {
        this.driveType.set(driveType);
    }

    public double getAngle() {
        return angle.get();
    }

    public DoubleProperty angleProperty() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle.set(angle);
    }


}
