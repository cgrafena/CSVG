package org.apache.batik.constraint.values;

import org.w3c.dom.Node;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;

/**
 * Represents a string based value which cannot be converted to any other type.
 */
public class UnconvertibleValue extends StringValue {

    /*
     * All types except the following are those which are handled
     * by UnconvertibleValue:
     *
     *   TYPE_STRING
     *   TYPE_BOOLEAN
     *   TYPE_INTEGER
     *   TYPE_NUMBER
     *   TYPE_LENGTH
     *   TYPE_COORDINATE
     *   TYPE_URI
     *   TYPE_FREQUENCY *
     *   TYPE_TIME *
     *   TYPE_GLYPH_ORIENTATION_HORIZONTAL_VALUE
     *   TYPE_NUMBER_OR_PERCENTAGE
     *   TYPE_TRANSFORM_LIST
     *   TYPE_MATRIX
     *   TYPE_RECT
     *   TYPE_POINT
     *   TYPE_NODESET *
     */

    /**
     * Create a new UnconvertibleValue.
     */
    public UnconvertibleValue(/*ConstraintEngine ce,*/ String s, int t) {
        super(/*ce,*/ s);
        type = t;
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        if (type == this.type) {
            return new UnconvertibleValue(/*constraintEngine,*/ value, type);
        } else if (type == TYPE_STRING) {
            return new StringValue(/*constraintEngine,*/ value);
        } else if (this.type == TYPE_UNKNOWN) {
            return Value.createValue(type, value, origin);
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        return v != null && type == v.type && toString().equals(v.toString());
    }
}
