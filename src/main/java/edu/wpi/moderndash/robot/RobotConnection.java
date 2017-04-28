package edu.wpi.moderndash.robot;

import edu.wpi.moderndash.property.EnumProperty;

import java.util.Optional;

import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;

import lombok.NonNull;

/**
 * Describes a connection to a robot.
 */
public interface RobotConnection {

    /**
     * Starts this connection.
     */
    void start();

    /**
     * Checks if this connection was started.
     */
    boolean isStarted();

    /**
     * Queries the robot for the value mapped to a key. This could be something like "BatteryVoltage",
     * "CurrentDrawPort13", "RobotState", etc. If there is no connection to the robot, or if no value
     * is associated with the given key, this will return {@link Optional#empty()}.
     *
     * @param key the name of the value being queried
     *
     * @return an Optional containing the value associated with the given key, or an empty Optional
     * if no such value exists
     */
    Optional<String> query(@NonNull String key);

    /**
     * Gets a property describing the team number of the robot the app is connected to.
     */
    ObservableIntegerValue teamNumberProperty();

    /**
     * Gets a property describing the IP address of the robot the app is connected to.
     */
    ObservableStringValue robotIpProperty();

    /**
     * Gets a property describing whether or not this connection is currently connected.
     */
    ObservableBooleanValue isConnectedProperty();

    /**
     * Gets a property describing the current state of the robot.
     */
    EnumProperty<RobotState> robotStateProperty();

    default int teamNumber() {
        return teamNumberProperty().get();
    }

    default String robotIP() {
        return robotIpProperty().get();
    }

    default boolean isConnected() {
        return isConnectedProperty().get();
    }

    default RobotState robotState() {
        return robotStateProperty().getValue();
    }

}
