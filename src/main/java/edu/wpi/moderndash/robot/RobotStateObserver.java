package edu.wpi.moderndash.robot;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.wpi.moderndash.property.EnumProperty;

import javafx.beans.binding.BooleanExpression;

import lombok.extern.java.Log;

import static edu.wpi.moderndash.robot.RobotState.AUTONOMOUS;
import static edu.wpi.moderndash.robot.RobotState.DISABLED;
import static edu.wpi.moderndash.robot.RobotState.DISCONNECTED;
import static edu.wpi.moderndash.robot.RobotState.TELEOP;
import static edu.wpi.moderndash.robot.RobotState.TEST;

/**
 * Observes the current state of the robot. For example, this can be used in a connection indicator:
 * <pre><code>
 *     ConnectionIndicator indicator = ...
 *     indicator.connectedProperty().bind(robotStateObserver.isDisconnected().not());
 * </code></pre>
 *
 * or perhaps in a bank of indicators
 *
 * <pre><code>
 *     StateIndicator auto = ...
 *     StateIndicator teleop = ...
 *     ...
 *     auto.selectedProperty().bind(robotStateObserver.inAutonomous());
 *     teleop.selectedProperty().bind(robotStateObserver.inTeleop());
 *     // etc.
 * </code></pre>
 *
 * This is an <i>injectable</i>, <i>singleton</i> class. It can be injected as a field (like for FXML controllers) with
 * <pre><code>
 *    {@literal @}Inject
 *     private RobotStateObserver robotStateObserver;
 * </code></pre> or into a constructor as an argument. If that constructor is marked with an {@code @Inject} annotation,
 * Guice will automatically inject it into the constructor every time it creates a new instance.
 */
@Log
@Singleton
public class RobotStateObserver {

    @Inject
    private RobotConnection connection;

    private EnumProperty<RobotState> state() {
        return connection.robotStateProperty();
    }

    /**
     * Gets the current robot state.
     */
    public RobotState getState() {
        return state().get();
    }

    public EnumProperty<RobotState> stateProperty() {
        return state();
    }

    public BooleanExpression isDisconnected() {
        return state().hasValue(DISCONNECTED);
    }

    public BooleanExpression isDisabled() {
        return state().hasValue(DISABLED);
    }

    public BooleanExpression inAutonomous() {
        return state().hasValue(AUTONOMOUS);
    }

    public BooleanExpression inTeleop() {
        return state().hasValue(TELEOP);
    }

    public BooleanExpression inTest() {
        return state().hasValue(TEST);
    }

}
