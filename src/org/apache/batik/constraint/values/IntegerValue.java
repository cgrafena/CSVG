package org.apache.batik.constraint.values;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLength;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.ConstraintException;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;

/**
 * Represents an integer value.
 */
public class IntegerValue extends Value {

    /**
     * The integer value.
     */
    int value;
    
    /**
     * Create a new IntegerValue.
     */
    public IntegerValue(/*ConstraintEngine ce,*/ int i) {
        super(/*ce,*/ TYPE_INTEGER);
        value = i;
    }

    /**
     * Create a new IntegerValue from a string.
     */
    public IntegerValue(/*ConstraintEngine ce,*/ String is) {
        super(/*ce,*/ TYPE_INTEGER);
        value = parseInteger(is);
    }
    
    /**
     * Get the integer value.
     */
    public int getInteger() {
        return value;
    }

    /**
     * Convert the given string to an integer.
     */
    public static int parseInteger(String value) throws TypeErrorException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            invalidStringConversion(value, TYPE_INTEGER);
        }
        return 0;
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_INTEGER:
                return new IntegerValue(/*constraintEngine,*/ value);
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ toString());
            case TYPE_NUMBER:
                return new NumberValue(/*constraintEngine,*/ value);
            case TYPE_LENGTH:
                return new LengthValue(/*constraintEngine,*/ origin, value);
            case TYPE_COORDINATE:
                return new CoordinateValue(/*constraintEngine,*/ origin, value);
            // case TYPE_ANGLE:
            case TYPE_GLYPH_ORIENTATION_HORIZONTAL_VALUE:
                // return new GlyphOrientationHorizontalValue(/*constraintEngine,*/ value);
                throw new ConstraintException("GlyphOrientationHorizontalValue not implemented yet");
            case TYPE_NUMBER_OR_PERCENTAGE:
                return new NumberOrPercentageValue(/*constraintEngine,*/ toString());
            case TYPE_NUMBER_OPTIONAL_NUMBER:
            case TYPE_BASELINE_SHIFT_VALUE:
            case TYPE_OPACITY_VALUE:
            case TYPE_FONT_SIZE_VALUE:
            case TYPE_FONT_SIZE_ADJUST_VALUE:
            case TYPE_GLYPH_ORIENTATION_VERTICAL_VALUE:
            case TYPE_KERNING_VALUE:
            case TYPE_SPACING_VALUE:
            case TYPE_LENGTH_OR_INHERIT:
            case TYPE_STROKE_MITERLIMIT_VALUE:
                return new UnconvertibleValue(/*constraintEngine,*/ toString(), type);
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof IntegerValue) {
            IntegerValue iv = (IntegerValue) v;
            return iv.value == value;
        }
        return false;
    }
    
    // Operators /////////////////////////////////////////////////////////////

    public XObject boolean_() throws TransformerException {
        return value == 0 ? XBoolean.S_FALSE : XBoolean.S_TRUE;
    }
    
    public XObject number() throws TransformerException {
        return new XNumber(value);
    }
}
