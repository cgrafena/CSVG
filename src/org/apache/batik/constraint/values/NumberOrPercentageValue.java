package org.apache.batik.constraint.values;

import org.w3c.dom.Node;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;

/**
 * Represents a number or percentage value.
 */
public class NumberOrPercentageValue extends Value {

    /**
     * The number or percentage value.
     */
    float value;
    
    /**
     * If the number is a percentage.
     */
    boolean isPercentage;

    /**
     * Create a new NumberOrPercentageValue.
     */
    public NumberOrPercentageValue(/*ConstraintEngine ce,*/ String s) {
        super(/*ce,*/ TYPE_NUMBER_OR_PERCENTAGE);
        if (s.charAt(s.length() - 1) == '%') {
            isPercentage = true;
            value = Float.parseFloat(s.substring(0, s.length() - 2));
        } else {
            isPercentage = false;
            value = Float.parseFloat(s);
        }
    }

    /**
     * Get the number of percentage value.
     */
    public float getNumber() {
        return value;
    }

    /**
     * Return whether the value is a percentage.
     */
    public boolean getIsPercentage() {
        return isPercentage;
    }
    
    /**
     * Return the string representation of this value.
     */
    public String toString() {
        if (isPercentage) {
            return Float.toString(value) + "%";
        } else {
            return Float.toString(value);
        }
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_NUMBER_OR_PERCENTAGE:
                return new NumberOrPercentageValue(/*constraintEngine,*/ toString());
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ toString());
            case TYPE_BASELINE_SHIFT_VALUE:
            case TYPE_FONT_SIZE_VALUE:
                return new UnconvertibleValue(/*constraintEngine,*/ toString(), type);
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof NumberOrPercentageValue) {
            NumberOrPercentageValue nopv = (NumberOrPercentageValue) v;
            return value == nopv.value && isPercentage == nopv.isPercentage;
        }
        return false;
    }
}
