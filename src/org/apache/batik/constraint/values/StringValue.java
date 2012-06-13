package org.apache.batik.constraint.values;

import org.w3c.dom.Node;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;

/**
 * Represents a string value.
 */
public class StringValue extends Value {

    /**
     * The string value.
     */
    String value;
    
    /**
     * Create a new StringValue.
     */
    public StringValue(/*ConstraintEngine ce,*/ String s) {
        super(/*ce,*/ TYPE_STRING);
        value = s;
    }

    /**
     * Get the string value.
     */
    public String getString() {
        return value;
    }

    /**
     * Return the string representation of this value.
     */
    public String toString() {
        return value;
    }

    /**
     * Convert this value to another type specifying an origin, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        return createValue(type, value, origin);
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        return v != null && toString().equals(v.toString());
    }

    /**
     * General equality function, used by builtin XPath operator classes.
     */
    public boolean equals(Object rhs) {
        if (rhs instanceof Value) {
            return equals((Value) rhs);
        }
        return toString().equals(rhs.toString());
    }
}
