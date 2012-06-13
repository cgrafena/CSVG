package org.apache.batik.constraint;

/**
 * An XPath related error.
 */
public class XPathException extends ConstraintException {

    /**
     * Create a new type error exception.
     */
    public XPathException() {
    }

    /**
     * Create a new type error exception.
     */
    public XPathException(String message) {
        super(message);
    }

    /**
     * Create a new type error exception.
     */
    public XPathException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new type error exception.
     */
    public XPathException(Throwable cause) {
        super(cause);
    }
}
