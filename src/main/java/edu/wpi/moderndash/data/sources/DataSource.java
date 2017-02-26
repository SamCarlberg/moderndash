package edu.wpi.moderndash.data.sources;

import edu.wpi.moderndash.data.Loggable;

public interface DataSource<T> extends Loggable<T> {

    /**
     * Gets the current value of this data source. May return {@code null} if this source isn't active,
     * but may also just return the most recent value.
     */
    T get();

    /**
     * Checks if this data source is active, i.e. its value may update at any time.
     *
     * @return true if this data source is active, false if not
     */
    boolean active();

}
