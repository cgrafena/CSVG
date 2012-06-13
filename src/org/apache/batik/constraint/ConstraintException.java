package org.apache.batik.constraint;

/**
 * An error caused by the constraint engine.
 */
public class ConstraintException extends RuntimeException {

    /**
     * Create a new type error exception.
     */
    public ConstraintException() {
    }

    /**
     * Create a new type error exception.
     */
    public ConstraintException(String message) {
        super(message);
    }

    /**
     * Create a new type error exception.
     */
    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new type error exception.
     */
    public ConstraintException(Throwable cause) {
        super(cause);
    }
}
