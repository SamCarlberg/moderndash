package edu.wpi.moderndash.robot;

import edu.wpi.moderndash.property.EnumProperty;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import lombok.extern.java.Log;

import static edu.wpi.moderndash.robot.RobotState.AUTONOMOUS;
import static edu.wpi.moderndash.robot.RobotState.DISABLED;
import static edu.wpi.moderndash.robot.RobotState.DISCONNECTED;
import static edu.wpi.moderndash.robot.RobotState.TELEOP;
import static edu.wpi.moderndash.robot.RobotState.TEST;

/**
 * Observes the current state of the robot.
 */
@Log
public class RobotStateObserver implements ChangeListener<RobotState> {

    private final EnumProperty<RobotState> state = new EnumProperty<>(this, "robot-state", DISCONNECTED);

    public RobotStateObserver() {
        state.addListener(this);
    }

    /**
     * Sets the current robot state.
     */
    public void observeStateChanged(RobotState newState) {
        state.set(newState);
    }

    /**
     * Gets the current robot state.
     */
    public RobotState getState() {
        return state.get();
    }

    public EnumProperty<RobotState> stateProperty() {
        return state;
    }

    public BooleanExpression isDisconnected() {
        return state.hasValue(DISCONNECTED);
    }

    public BooleanExpression isDisabled() {
        return state.hasValue(DISABLED);
    }

    public BooleanExpression inAutonomous() {
        return state.hasValue(AUTONOMOUS);
    }

    public BooleanExpression inTeleop() {
        return state.hasValue(TELEOP);
    }

    public BooleanExpression inTest() {
        return state.hasValue(TEST);
    }

    @Override
    public void changed(ObservableValue<? extends RobotState> observable, RobotState oldState, RobotState newState) {
        log.info(String.format("Robot state changed from %s to %s", oldState, newState));
    }

}
