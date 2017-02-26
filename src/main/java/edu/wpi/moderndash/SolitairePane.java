package edu.wpi.moderndash;

import com.google.common.collect.ImmutableMap;

import java.util.List;

import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

import lombok.extern.java.Log;

import static edu.wpi.moderndash.util.NodeUtils.findNearestNode;

/**
 * A solitaire pane arranges its contents in columns that can be rearranged via drag-and-drop.
 * All nodes added to this pane will be given drag-and-drop handlers to support this. As a
 * side effect, <i>all previous handlers will be removed</i>. If a {@link javafx.scene.layout.Region Region}
 * is added (eg {@link javafx.scene.layout.VBox VBox}, {@link javafx.scene.control.TitledPane, TitledPane}),
 * any drag-and-drop handlers of its children will be unaffected.
 */
@Log
@DefaultProperty("children")
public class SolitairePane extends HBox {

    // Used to make sure that only DnD from THIS solitaire pane will occur.
    // Otherwise, we could get DnD from other solitaire panes and chaos could result
    private final int idHash = System.identityHashCode(this);
    private final String DRAG_KEY = "solitaire-pane-drag-key-" + idHash;
    private final DataFormat DRAG_FORMAT = new DataFormat("solitaire-drag-target-" + idHash);

    /**
     * The control being dragged.
     */
    private Node dragging = null;

    /**
     * The original index of the control being dragged.
     * Used when DnD fails or is cancelled.
     */
    private int draggingIndex = 0;

    /**
     * Creates a new solitaire pane.
     */
    public SolitairePane() {
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                List<? extends Node> added = c.getAddedSubList();
                added.forEach(this::makeDraggable);
            }
        });

        // Allow nodes to be directly dragged onto this pane
        this.setOnDragOver(event -> {
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(DRAG_FORMAT)
                    && DRAG_KEY.equals(dragboard.getContent(DRAG_FORMAT))
                    && dragging != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        // Drop dragged nodes onto this pane
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(DRAG_FORMAT)) {
                Node nearestNode = findNearestNode(getChildren(), event.getX(), event.getY());
                if (nearestNode == null) {
                    getChildren().add(dragging);
                } else {
                    int targetIndex = getChildren().indexOf(nearestNode);
                    if (event.getX() > nearestNode.getLayoutX()) {
                        // dropped to the right of the node
                        // bump the index to place it to that side
                        targetIndex++;
                    }
                    getChildren().add(targetIndex, dragging);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Makes a node draggable within this solitaire pane, as well as making it a
     * valid drop target for other nodes in this pane.
     * <br>
     * <strong>This will override existing drag-and-drop handlers</strong>
     */
    private void makeDraggable(Node node) {
        // Start dragging this node
        node.setOnDragDetected(e -> {
            Dragboard dragboard = node.startDragAndDrop(TransferMode.MOVE);
            dragboard.setContent(ImmutableMap.of(DRAG_FORMAT, DRAG_KEY));
            dragboard.setDragView(node.snapshot(null, null));
            dragging = node;
            draggingIndex = getChildren().indexOf(node);
            getChildren().remove(node);
            e.consume();
        });

        // Allow other nodes to be dragged and dropped onto this one
        node.setOnDragOver(e -> {
            final Dragboard dragboard = e.getDragboard();
            if (dragboard.hasContent(DRAG_FORMAT)
                    && DRAG_KEY.equals(dragboard.getContent(DRAG_FORMAT))
                    && dragging != null) {
                e.acceptTransferModes(TransferMode.MOVE);
                e.consume();
            }
        });

        // Drops a moved node onto this one and reorders the nodes
        node.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasContent(DRAG_FORMAT)) {
                int targetIndex = getChildren().indexOf(node);
                if (e.getX() / node.getLayoutBounds().getWidth() > 0.5) {
                    // Dropped in the right half of the node, bump the index
                    // to place it to the right
                    targetIndex++;
                }
                getChildren().add(Math.min(targetIndex, getChildren().size()), dragging);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });

        node.setOnDragDone(e -> {
            // Put the dragged node back where it started if the drop wasn't accepted
            if (!e.isAccepted()) {
                getChildren().add(draggingIndex, dragging);
            }
        });
    }

}
