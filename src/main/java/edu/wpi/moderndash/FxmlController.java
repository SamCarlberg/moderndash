package edu.wpi.moderndash;

import javafx.fxml.FXML;

/**
 * Interface for classes that control an FXML-defined UI component.
 */
public interface FxmlController {

    /**
     * Called after all fields marked with {@code @FXML} are injected. This is used to execute code that a constructor
     * normally would, but is not able to do due to the injected fields not being present until after the constructor
     * runs.
     */
    @FXML
    void initialize() throws Exception;

}
