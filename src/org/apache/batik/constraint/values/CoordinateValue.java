package org.apache.batik.constraint.values;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLength;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;

/**
 * Represents a coordinate value.
 */
public class CoordinateValue extends LengthValue {

    /**
     * Create a new CoordinateValue.
     */
    public CoordinateValue(/*ConstraintEngine ce,*/ Node n, float f) {
        super(/*ce,*/ n, TYPE_COORDINATE);
        value = parseLength(Float.toString(f));
    }

    /**
     * Create a new CoordinateValue.
     */
    public CoordinateValue(/*ConstraintEngine ce,*/ Node n, String s) {
        super(/*ce,*/ n, TYPE_COORDINATE);
        value = parseLength(s);
    }
    
    /**
     * Get the coordinate value.
     */
    public SVGLength getCoordinate() {
        return value;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return value.getValueAsString();
    }
}
