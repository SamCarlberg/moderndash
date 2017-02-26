package edu.wpi.moderndash;


import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import lombok.NonNull;

/**
 * A pane that places its content it a titled border, similar to an application window.
 */
@DefaultProperty("content")
public class InternalWindow extends BorderPane {

    private static final PseudoClass FOCUSED = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass HIGHLIGHTED = PseudoClass.getPseudoClass("highlighted");

    private boolean highlighted = false;

    private boolean RESIZE_BOTTOM;
    private boolean RESIZE_RIGHT;

    private StringProperty title = new SimpleStringProperty(this, "title", "");
    private ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content", new StackPane());
    private BooleanProperty closeable = new SimpleBooleanProperty(this, "closeable", false);

    public InternalWindow() {
        this("", new StackPane());
    }

    public InternalWindow(@NonNull String title, @NonNull Node content) {
        final BorderPane titleBar = new BorderPane();
        final Label titleLabel = new Label();
        final Button closeButton = new Button();
        final ContextMenu contextMenu = new ContextMenu();
        titleBar.setLeft(titleLabel);
        titleBar.setRight(closeButton);
        setTop(titleBar);
        centerProperty().bind(contentProperty());
        titleLabel.textProperty().bind(titleProperty());

        contentProperty().addListener((obs, o, n) -> {
            n.focusedProperty().addListener((a, b, c) -> this.toFront());
            if (o != null) {
                o.getStyleClass().remove("content");
            }
            n.getStyleClass().add("content");
        });

        setTitle(title);
        setContent(content);

        getStylesheets().addAll("/edu/wpi/moderndash/main.css", "/edu/wpi/moderndash/internal-window.css");
        getStyleClass().add("internal-window");
        titleBar.getStyleClass().add("title-bar");
        titleLabel.getStyleClass().add("title");
        closeButton.getStyleClass().add("close-button");


        makeDraggable(titleBar, titleLabel, this);
        makeResizable(20);

        MenuItem highlight = new MenuItem("Highlight");
        highlight.setOnAction(e -> toggleHighlight());
        contextMenu.getItems().addAll(highlight);

        titleBar.setOnContextMenuRequested(e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));
        titleBar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                toggleHighlight();
            }
        });

        setOnMouseClicked(e -> toFront());
        setOnMousePressed(e -> pseudoClassStateChanged(FOCUSED, true));
        setOnMouseReleased(e -> pseudoClassStateChanged(FOCUSED, false));

        closeButton.setOnAction(e -> {
            if (getParent() == null || !(getParent() instanceof Pane)) {
                return;
            }
            ((Pane) getParent()).getChildren().remove(this);
        });
        closeButton.visibleProperty().bind(closeable);
    }

    private void toggleHighlight() {
        highlighted = !highlighted;
        pseudoClassStateChanged(HIGHLIGHTED, highlighted);
    }

    public void makeDraggable(@NonNull Node... handles) {
        for (Node handle : handles) {
            Delta d = new Delta();
            handle.setOnMousePressed(e -> {
                d.x = getLayoutX() - e.getScreenX();
                d.y = getLayoutY() - e.getScreenY();
                toFront();
            });
            handle.setOnMouseDragged(e -> {
                double x = d.x + e.getScreenX();
                double y = d.y + e.getScreenY();
                if (x > 0 && x + getWidth() < ((Region) getParent()).getWidth()) {
                    setLayoutX(x);
                }
                if (y > 0 && y + getHeight() < ((Region) getParent()).getHeight()) {
                    setLayoutY(y);
                }
            });
        }
    }

    public void makeResizable(double mouseBorderWidth) {
        setOnMouseMoved(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            double width = boundsInLocalProperty().get().getWidth();
            double height = boundsInLocalProperty().get().getHeight();
            // if on the edge, change state and cursor
            if (Math.abs(mouseX - width) < mouseBorderWidth
                    && Math.abs(mouseY - height) < mouseBorderWidth) {
                RESIZE_RIGHT = true;
                RESIZE_BOTTOM = true;
                this.setCursor(Cursor.NW_RESIZE);
            } else {
                RESIZE_BOTTOM = false;
                RESIZE_RIGHT = false;
                this.setCursor(Cursor.DEFAULT);
            }

        });
        setOnMouseDragged(mouseEvent -> {
            // resize root
            Region region = (Region) getChildren().get(0);
            // resize logic depends on state
            if (RESIZE_BOTTOM && RESIZE_RIGHT) {
                region.setPrefSize(mouseEvent.getX(), mouseEvent.getY());
            } else if (RESIZE_RIGHT) {
                region.setPrefWidth(mouseEvent.getX());
            } else if (RESIZE_BOTTOM) {
                region.setPrefHeight(mouseEvent.getY());
            }
        });
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title.set(title);
    }

    public String getTitle() {
        return title.get();
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(@NonNull Node content) {
        this.content.set(content);
    }

    public Node getContent() {
        return content.get();
    }

    public BooleanProperty closeableProperty() {
        return closeable;
    }

    public boolean isCloseable() {
        return closeable.get();
    }

    public void setCloseable(boolean closeable) {
        this.closeable.set(closeable);
    }

    private static final class Delta {

        public double x, y;
    }

}
