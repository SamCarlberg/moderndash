package edu.wpi.moderndash.exception;

import lombok.NonNull;

/**
 * An exception thrown when trying to get or put a value to NetworkTables that cannot be stored.
 */
public class IllegalNetworkTableType extends RuntimeException {

    public IllegalNetworkTableType(@NonNull Class<?> type) {
        this("Values of type " + type.getName() + " cannot be stored in NetworkTables");
    }

    public IllegalNetworkTableType(String message) {
        super(message);
    }

}
