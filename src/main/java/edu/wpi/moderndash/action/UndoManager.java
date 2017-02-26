package edu.wpi.moderndash.action;

import java.util.LinkedList;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An UndoManager keeps track of actions that have been performed, either by a user or internally.
 */
public class UndoManager {

    /**
     * The types of action.
     */
    private enum ActionType {
        UNDO, REDO
    }

    /**
     * An action that keeps track of which of (undo, redo) it can execute. This
     * purposefully does not implement {@link Action} because it doesn't have
     * a notion of an "act"; it can only tell another {@code Action} to undo or redo.
     */
    @RequiredArgsConstructor
    @SuppressWarnings("WeakerAccess")
    private static class UndoRedoAction {

        private final @NonNull Action action; // delegate the actions
        private ActionType nextType = ActionType.UNDO;

        public void undo() {
            if (!canUndo()) {
                // Sanity check
                throw new IllegalStateException("Cannot undo");
            }
            action.undo();
            nextType = ActionType.REDO;
        }

        public void redo() {
            if (!canRedo()) {
                // Sanity check
                throw new IllegalStateException("Cannot redo");
            }
            action.redo();
            nextType = ActionType.UNDO;
        }

        public boolean canUndo() {
            return nextType == ActionType.UNDO;
        }

        public boolean canRedo() {
            return nextType == ActionType.REDO;
        }

    }

    private static final int HEAD = 0;
    private final ObservableList<UndoRedoAction> actions = FXCollections.observableList(new LinkedList<>());
    private final ObjectProperty<UndoRedoAction> currentAction = new SimpleObjectProperty<>(this, "currentAction", null);
    private final IntegerProperty current = new SimpleIntegerProperty(this, "current", HEAD);
    private final BooleanProperty canUndo = new SimpleBooleanProperty(this, "canUndo", false);
    private final BooleanProperty canRedo = new SimpleBooleanProperty(this, "canRedo", true);
    private final Invalidator invalidator = new Invalidator();

    public UndoManager() {
        Observable[] dependencies = {actions, current, currentAction, invalidator};
        canUndo.bind(Bindings.createBooleanBinding(() -> !actions.isEmpty() && canUndo0(), dependencies));
        canRedo.bind(Bindings.createBooleanBinding(() -> !actions.isEmpty() && canRedo0(), dependencies));

        // Sanity check
        current.addListener((obs, o, n) -> {
            final int i = n.intValue();
            if (i > size()) {
                throw new IndexOutOfBoundsException(i + " > " + size());
            }
            if (i < HEAD) {
                throw new IndexOutOfBoundsException(i + " < " + HEAD);
            }
        });
    }

    /**
     * Checks that the current action isn't the oldest one we're keeping track of.
     */
    private boolean notAtEnd() {
        return current.get() < actions.size() - 1;
    }

    /**
     * Checks that the current action isn't the newest one we're keeping track of.
     */
    private boolean notAtBeginning() {
        return current.get() > HEAD;
    }

    private boolean canUndo0() {
        UndoRedoAction a = currentAction.get();
        return a != null && (a.canUndo() || notAtEnd());
    }

    private boolean canRedo0() {
        UndoRedoAction a = currentAction.get();
        return a != null && (a.canRedo() || notAtBeginning());
    }

    /**
     * Gets the number of actions this manager has observed.
     */
    public int size() {
        return actions.size();
    }

    /**
     * Observes an action and adds it to the queue of actions to be able to move between states.
     *
     * @param action the action to observe
     */
    public void observe(@NonNull Action action) {
        if (canRedo()) {
            // Head action is in the wrong state, remove it
            actions.remove(HEAD);
        }
        if (current.get() != HEAD) {
            // Still at least one action back. Remove the "orphan" actions.
            actions.remove(HEAD, current.get());
            current.set(HEAD);
        }
        UndoRedoAction a = new UndoRedoAction(action);
        actions.add(HEAD, a);
        currentAction.setValue(a);
    }

    /**
     * Undoes the most recent action.
     *
     * @return true if an action was undone, false otherwise
     */
    public boolean undo() {
        if (canUndo()) {
            if (!currentAction.get().canUndo()) {
                // Can undo, but we have to use the previous action to do so
                current.set(current.get() + 1);
                currentAction.set(actions.get(current.get()));
            }
            invalidator.invalidate();
            currentAction.get().undo();
            return true;
        }
        return false;
    }

    /**
     * Redoes the most recently undone action.
     *
     * @return true if an action was redone, false otherwise
     */
    public boolean redo() {
        if (canRedo()) {
            if (!currentAction.get().canRedo()) {
                // Can redo, but we have to use the newer action to do so
                current.set(current.get() - 1);
                currentAction.set(actions.get(current.get()));
            }
            invalidator.invalidate();
            currentAction.get().redo();
            return true;
        }
        return false;
    }

    public ReadOnlyBooleanProperty canUndoProperty() {
        return canUndo;
    }

    /**
     * Checks if there is an action this manager can undo.
     */
    public boolean canUndo() {
        return canUndoProperty().get();
    }

    public ReadOnlyBooleanProperty canRedoProperty() {
        return canRedo;
    }

    /**
     * Checks if there is an action this manager can redo ("un-undo").
     */
    public boolean canRedo() {
        return canRedoProperty().get();
    }

}
