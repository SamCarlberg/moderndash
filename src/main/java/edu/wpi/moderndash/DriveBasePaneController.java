package edu.wpi.moderndash;

import edu.wpi.moderndash.views.DriveBaseView;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DriveBasePaneController implements FxmlController {

    @FXML
    private VBox leftSideExtras; // extra sensor views go here and on the right side
    @FXML
    private BorderPane center; // where the drive base is actually located
    @FXML
    private VBox rightSideExtras; // extra sensor views go here

    @FXML
    private DriveBaseView driveBaseView;

    @FXML
    @Override
    public void initialize() {

    }

}
