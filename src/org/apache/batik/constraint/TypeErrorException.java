package org.apache.batik.constraint;

/**
 * A type error.
 */
public class TypeErrorException extends ConstraintException {

    /**
     * Create a new type error exception.
     */
    public TypeErrorException() {
    }

    /**
     * Create a new type error exception.
     */
    public TypeErrorException(String message) {
        super(message);
    }

    /**
     * Create a new type error exception.
     */
    public TypeErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new type error exception.
     */
    public TypeErrorException(Throwable cause) {
        super(cause);
    }
}
