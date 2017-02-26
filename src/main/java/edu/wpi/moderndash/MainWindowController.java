package edu.wpi.moderndash;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import lombok.extern.java.Log;

@Log
public class MainWindowController implements FxmlController {

    @FXML
    private SplitPane splitPane;
    @FXML
    private HBox systemsPane;
    @FXML
    private Pane driveBasePane;
    @FXML
    private HBox footer;
    private Node about;
    private AutoHideDialog aboutDialog;

    @FXML
    @Override
    public void initialize() throws IOException {
        // init the about dialog stuff
        about = FXMLLoader.load(MainWindowController.class.getResource("About.fxml"));
        aboutDialog = new AutoHideDialog("About FRC moderndash");
        aboutDialog.setContent(about);
    }

    @FXML
    public void close() {
        log.info("Close button pressed, exiting app");
        System.exit(0);
    }

    @FXML
    public void about() {
        aboutDialog.showAndWait();
    }

}
