package edu.wpi.moderndash.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import lombok.NonNull;

public interface Action {

    /**
     * Performs this action.
     */
    void act();

    /**
     * Undoes this action. This should do the opposite of {@link #act()}; after calling {@code undo()},
     * the state of the application should be as if the action was never run at all.
     */
    void undo();

    /**
     * Re-does an undo. Defaults to {@link #act()}.
     */
    default void redo() {
        act();
    }

    /**
     * An action that does nothing ("no-op", or "nop").
     */
    Action NOP = new NopAction();

    /**
     * Creates an action that is equivalent to running this action the given number of times.
     * Returns {@link #NOP} if the number of times to run is zero or negative.
     *
     * @param amount the number of times this action should be performed
     */
    default Action times(int amount) {
        if (amount < 1) {
            return NOP;
        }
        return of(() -> IntStream.range(0, amount).sequential().forEach(i -> act()),
                  () -> IntStream.range(0, amount).sequential().forEach(i -> undo())
        );
    }

    /**
     * Creates an action from an {@link #act() act} function and an {@link #undo() undo} function.
     *
     * @param act  the function that this action performs
     * @param undo the function to undo
     */
    static Action of(@NonNull Runnable act,
                     @NonNull Runnable undo) {
        return new ActionDelegate(act, undo);
    }

    /**
     * Creates an action that's an aggregate of the given actions.
     *
     * @param actions the actions to aggregate
     *
     * @throws NullPointerException if given a null array (eg {@code aggregate((Action[]) null)}) or if any of the
     *                              actions are null
     */
    static Action aggregate(@NonNull Action... actions) {
        if (actions.length == 0) {
            return NOP;
        }
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == null) {
                throw new NullPointerException("Null action at index " + i);
            }
        }
        List<Action> forward = Arrays.asList(actions.clone());
        List<Action> backward = Arrays.asList(actions.clone());
        Collections.reverse(backward);
        return of(() -> forward.forEach(Action::act),
                  () -> backward.forEach(Action::undo));
    }

}
