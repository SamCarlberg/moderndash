package edu.wpi.moderndash;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import lombok.NonNull;

/**
 * An undecorated dialog that will automatically hide itself when it loses focus or the escape button is pressed.
 */
public class AutoHideDialog {

    private final Label titleLabel = new Label();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
    private final BorderPane pane = new BorderPane();

    private final Stage stage = new Stage();

    /**
     * Creates a new autohiding dialog with no title and no content.
     */
    public AutoHideDialog() {
        pane.getStylesheets().add("/edu/wpi/moderndash/css/autohide-dialog.css");
        pane.getStyleClass().add("autohide-dialog-pane");
        titleLabel.getStyleClass().add("autohide-dialog-title");
        titleLabel.textProperty().bind(title);
        pane.centerProperty().bind(content);
        pane.topProperty().set(titleLabel);
        pane.setPrefSize(250, 180);
        stage.setScene(new Scene(pane));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // Center-align content
        content.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getStyleClass().remove("autohide-dialog-content");
            }
            if (newValue != null) {
                newValue.getStyleClass().add("autohide-dialog-content");
                BorderPane.setAlignment(newValue, Pos.CENTER);
            }
        });

        // Hide on focus lost
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                hide();
            }
        });

        // Hide on escape pressed
        pane.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                hide();
            }
        });
    }

    /**
     * Creates a new autohiding dialog with the given title and no content.
     *
     * @param title the title of the dialog
     */
    public AutoHideDialog(String title) {
        this();
        this.title.set(title);
    }

    public Node getContent() {
        return content.get();
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(@NonNull Node content) {
        this.content.set(content);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Attempts to show this dialog.
     */
    public void show() {
        stage.requestFocus();
        pane.requestFocus();
        stage.show();
    }

    /**
     * Shows this dialog and blocks the calling thread until it's closed or hidden.
     */
    public void showAndWait() {
        stage.requestFocus();
        pane.requestFocus();
        stage.showAndWait();
    }

    /**
     * Hides this dialog.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Closes this dialog.
     */
    public void close() {
        stage.close();
    }

}
