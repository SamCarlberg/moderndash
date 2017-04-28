package edu.wpi.moderndash.data.sources;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * A data source provides some kin
 *
 * @param <T>
 */
public interface DataSource<T> {

    static <T> DataSource<T> none() {
        return new AbstractDataSource<T>() {};
    }

    /**
     * Checks if this data source is active, i.e. its value may update at any time.
     *
     * @return true if this data source is active, false if not
     */
    ObservableValue<Boolean> activeProperty();

    default boolean isActive() {
        return activeProperty().getValue();
    }

    ObservableValue<String> nameProperty();

    default String getName() {
        return nameProperty().getValue();
    }

    Property<T> dataProperty();

    /**
     * Gets the current value of this data source. May return {@code null} if this source isn't active,
     * but may also just return the most recent value.
     */
    default T getData() {
        return dataProperty().getValue();
    }

    default void setData(T newValue) {
        dataProperty().setValue(newValue);
    }

}
