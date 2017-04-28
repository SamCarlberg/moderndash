package edu.wpi.moderndash.views;

import com.google.inject.Inject;

import edu.wpi.moderndash.FxmlController;
import edu.wpi.moderndash.robot.RobotConnection;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import lombok.extern.java.Log;

@Log
public class ConnectionIndicatorController implements FxmlController {

    private final PseudoClass CONNECTED = PseudoClass.getPseudoClass("connected");

    @Inject
    private RobotConnection connection;

    @FXML
    private Pane root;
    @FXML
    private Label label;

    @Override
    public void initialize() {
        onConnection(connection.isConnected());
        connection.isConnectedProperty().addListener((observable, wasConnected, isConnected) -> {
            onConnection(isConnected);
        });
    }

    private void onConnection(boolean isConnected) {
        root.pseudoClassStateChanged(CONNECTED, isConnected);
        if (isConnected) {
            label.setText("Connected to robot at " + connection.robotIP());
        } else {
            label.setText("No connection to " + connection.teamNumber());
        }
    }

}
