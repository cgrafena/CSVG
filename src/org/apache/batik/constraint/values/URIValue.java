package org.apache.batik.constraint.values;

import org.w3c.dom.Node;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;

/**
 * Represents a URI value.
 */
public class URIValue extends StringValue {

    /**
     * Create a new URIValue.
     */
    public URIValue(/*ConstraintEngine ce,*/ String s) {
        super(/*ce,*/ s);
        type = TYPE_URI;
    }

    /**
     * Get the URI value.
     */
    public String getURI() {
        return value;
    }

    /**
     * Return the string representation of this value.
     */
    public String toString() {
        return value;
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_URI:
                return new URIValue(/*constraintEngine,*/ value);
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ value);
            case TYPE_OPTIONAL_URI:
            case TYPE_CURSOR_VALUE:
                return new UnconvertibleValue(/*constraintEngine,*/ value, type);
        }
        invalidConversion(this.type, type);
        return null;
    }
}
