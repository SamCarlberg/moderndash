package edu.wpi.moderndash.exception;

/**
 * An exception thrown when a view definition is invalid.
 */
public class InvalidViewException extends RuntimeException {

    public InvalidViewException(String message) {
        super(message);
    }

    public InvalidViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
