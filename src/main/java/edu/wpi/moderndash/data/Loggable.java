package edu.wpi.moderndash.data;

import javafx.collections.ObservableMap;

/**
 * @param <T> the type of data that gets logged
 */
public interface Loggable<T> {

    /**
     * Gets the data store for logged data. Logged data is mapped to its timestamp, which is the time in seconds
     * since the loggable was created the data was logged. For example, if this loggable was created at time
     * <i>T<sub>0</sub></i>, data is mapped to the time <i>T<sub>n</sub></i> it was logged, where
     * <i>T<sub>n</sub> + T<sub>0</sub></i> is the absolute time since the application was started.
     */
    ObservableMap<Double, T> getDataStore();

    /**
     * Logs the given data. Defaults to adding it to the {@link #getDataStore() data store}.
     *
     * @param data the data to log. This cannot be null.
     */
    void log(T data);

}
