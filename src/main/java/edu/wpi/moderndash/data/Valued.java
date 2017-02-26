package edu.wpi.moderndash.data;

import javafx.beans.property.Property;

/**
 * Interface for a valued type.
 *
 * @param <T> the type of the data values
 */
public interface Valued<T> {

    /**
     * Gets the property for the data value.
     */
    Property<T> valueProperty();

    /**
     * Sets the data value.
     */
    default void setValue(T value) {
        valueProperty().setValue(value);
    }

    /**
     * Gets the data value.
     */
    default T getValue() {
        return valueProperty().getValue();
    }

}
