package edu.wpi.moderndash.action;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * A simple implementation of {@link Observable} that allows for all listeners to be notified
 * of an invalidation when desired.
 */
public class Invalidator implements Observable {

    private List<InvalidationListener> invalidationListeners = new ArrayList<>();

    /**
     * Immediately notifies all listeners of an invalidation.
     */
    public void invalidate() {
        invalidationListeners.forEach(l -> l.invalidated(this));
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
    }
}
