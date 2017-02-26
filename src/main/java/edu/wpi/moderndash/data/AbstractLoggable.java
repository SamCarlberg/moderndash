package edu.wpi.moderndash.data;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A default implementation of {@code Loggable}.
 *
 * @param <T> the type of the data to be logged
 */
public abstract class AbstractLoggable<T> implements Loggable<T> {

    private final ObservableMap<Double, T> dataStore = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    @Override
    public ObservableMap<Double, T> getDataStore() {
        return dataStore;
    }

    @Override
    public void log(T data) {
        if (!stopwatch.isRunning()) {
            stopwatch.start();
        }
        dataStore.put((double) stopwatch.elapsed(TimeUnit.SECONDS), data);
    }

}

