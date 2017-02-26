package edu.wpi.moderndash;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import lombok.extern.java.Log;


/**
 * Controller for the upper half of the UI responsible for the minor subsystems.
 */
@Log
public class MinorSystemsPaneController implements FxmlController {

    @FXML
    private SolitairePane root;
    @FXML
    private TitledPane ungrouped;

    @FXML
    @Override
    public void initialize() {
        root.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    c.getAddedSubList().forEach(n -> {
                        HBox.setHgrow(n, Priority.ALWAYS);
                        VBox.setVgrow(n, Priority.ALWAYS);
                    });
                }
            }
        });
        root.getChildren().forEach(n -> {
            HBox.setHgrow(n, Priority.ALWAYS);
            VBox.setVgrow(n, Priority.ALWAYS);
        });
    }

}
