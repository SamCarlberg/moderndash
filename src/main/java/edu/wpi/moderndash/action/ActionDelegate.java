package edu.wpi.moderndash.action;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link Action} that delegates {@link #act()} and {@link #undo()} to
 * {@link Runnable Runnables} passed in.
 */
@RequiredArgsConstructor
class ActionDelegate implements Action {

    private final @NonNull Runnable act;
    private final @NonNull Runnable undo;

    @Override
    public void act() {
        act.run();
    }

    @Override
    public void undo() {
        undo.run();
    }

}
