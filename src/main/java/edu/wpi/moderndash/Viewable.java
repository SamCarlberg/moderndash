package edu.wpi.moderndash;

import edu.wpi.moderndash.data.Valued;

import javafx.scene.control.Control;

/**
 * @param <T> the data type that's viewed
 * @param <C> the type of the control for viewing data
 */
public interface Viewable<T, C extends Control & Valued<? super T>> {

    /**
     * Gets the JavaFX control for viewing this object.
     */
    C getControl();

}
