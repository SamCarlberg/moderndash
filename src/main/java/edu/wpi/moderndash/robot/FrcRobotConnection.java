package edu.wpi.moderndash.robot;

import com.google.common.annotations.VisibleForTesting;

import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.moderndash.Preferences;
import edu.wpi.moderndash.concurrent.DaemonThread;
import edu.wpi.moderndash.io.IPResolver;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class FrcRobotConnection implements RobotConnection {

    /**
     * Connection timeout, in milliseconds. A connection normally takes 2ms-10ms to determine with a RoboRIO.
     */
    private static final int TIMEOUT = 50; // millis

    private final ITable dataTable;
    private final Preferences preferences;
    private final IPResolver ipResolver;

    private final IntegerProperty teamNumber = new SimpleIntegerProperty(this, "team-number", 0);
    private final StringProperty robotIp = new SimpleStringProperty(this, "robot-ip", "");
    private final BooleanProperty isConnected = new SimpleBooleanProperty(this, "is-connected", false);
    private final ObjectProperty<RobotState> robotState = new SimpleObjectProperty<>(this, "robot-state", RobotState.UNKNOWN);


    private boolean started = false;

    public FrcRobotConnection(@NonNull ITable dataTable,
                              @NonNull Preferences preferences,
                              @NonNull IPResolver ipResolver) {
        this.dataTable = dataTable;
        this.preferences = preferences;
        this.ipResolver = ipResolver;
        teamNumber.bind(preferences.teamNumberProperty());
        robotIp.bind(Bindings.createStringBinding(this::makeIp, teamNumber, preferences.useMdnsProperty(), isConnected));
        isConnected.addListener((o, wasConnected, isConnected) -> {
            log.info("wasConnected: " + wasConnected + ", isConnected: " + isConnected);
            if (!isConnected) {
                robotState.set(RobotState.DISCONNECTED);
            }
        });
    }

    @VisibleForTesting
    void setConnected(boolean connected) {
        isConnected.set(connected);
    }

    private String makeIp() {
        int teamNumber = teamNumber();
        boolean mdns = preferences.useMdns();
        if (mdns) {
            return String.format("roborio-%d-frc.local", teamNumber);
        } else {
            return String.format("10.%d.%d.2", teamNumber / 100, teamNumber % 100);
        }
    }

    @Override
    public void start() {
        // Try to find a connection every 500 milliseconds
        Executors.newSingleThreadScheduledExecutor(DaemonThread::new).scheduleAtFixedRate(() -> {
            log.info("Running connection daemon");
            boolean resolved = ipResolver.canResolve(robotIP(), TIMEOUT, TimeUnit.MILLISECONDS);
            isConnected.set(resolved);
            if (!resolved) {
                robotState.set(RobotState.DISCONNECTED);
            }
        }, 0L, 500, TimeUnit.MILLISECONDS);

        // Listen for changes to the "State" key in the RobotInfo table
        dataTable.addTableListenerEx("State", (source, key, value, isNew) -> {
            if (value instanceof String) {
                try {
                    RobotState old = robotState();
                    robotState.set(RobotState.valueOf((String) value));
                    log.info("Robot state changed from " + old + " to " + robotState());
                } catch (IllegalArgumentException e) {
                    log.warning("Unexpected state: " + value);
                    robotState.set(RobotState.UNKNOWN);
                }
            } else {
                log.warning("Robot state value isn't a String (is: " + value + ", type = " + value.getClass().getSimpleName() + ")");
                robotState.set(RobotState.UNKNOWN);
            }
        }, 0xFF);
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Optional<String> query(@NonNull String key) {
        if (!isConnected()) {
            return Optional.empty();
        }
        try {
            return Optional.of(dataTable.getString(key));
        } catch (NetworkTableKeyNotDefined e) {
            // This gets thrown if:
            //  a. there is no value with key $key; or
            //  b. there is a value, but is not a String
            return Optional.empty();
        }
    }

    @Override
    public ObservableIntegerValue teamNumberProperty() {
        return teamNumber;
    }

    @Override
    public ObservableStringValue robotIpProperty() {
        return robotIp;
    }

    @Override
    public ObservableBooleanValue isConnectedProperty() {
        return isConnected;
    }

    @Override
    public ObservableValue<RobotState> robotStateProperty() {
        return robotState;
    }

}
