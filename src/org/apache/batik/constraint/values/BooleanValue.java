package org.apache.batik.constraint.values;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Represents a boolean value.
 */
public class BooleanValue extends Value {

    /**
     * The boolean value.
     */
    boolean value;
    
    /**
     * Create a new BooleanValue.
     */
    public BooleanValue(/*ConstraintEngine ce,*/ boolean b) {
        super(/*ce,*/ TYPE_BOOLEAN);
        value = b;
    }

    /**
     * Create a new BooleanValue from a string.
     */
    public BooleanValue(/*ConstraintEngine ce,*/ String bs) {
        super(/*ce,*/ TYPE_BOOLEAN);
        value = parseBoolean(bs);
    }
    
    /**
     * Get the boolean value.
     */
    public boolean getBoolean() {
        return value;
    }

    /**
     * Convert the given string to a boolean.
     */
    public static boolean parseBoolean(String value) throws TypeErrorException {
        if (value.equals("true"))
            return true;
        else if (value.equals("false"))
            return false;
        invalidStringConversion(value, TYPE_BOOLEAN);
        return false;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return value ? "true" : "false";
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_BOOLEAN:
                return new BooleanValue(/*constraintEngine,*/ value);
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ toString());
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof BooleanValue) {
            BooleanValue bv = (BooleanValue) v;
            return bv.value == value;
        }
        return false;
    }
    
    // Operators /////////////////////////////////////////////////////////////

    public XObject boolean_() throws TransformerException {
        return value ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    
    public XObject number() throws TransformerException {
        return value ? new XNumber(1.0) : new XNumber(0.0);
    }
}
