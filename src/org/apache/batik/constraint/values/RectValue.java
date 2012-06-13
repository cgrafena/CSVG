package org.apache.batik.constraint.values;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMRect;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Represents a rect value.
 */
public class RectValue extends Value {

    /**
     * The rect value.
     */
    SVGRect value;
    
    /**
     * Create a new RectValue.
     */
    public RectValue(/*ConstraintEngine ce,*/ float x, float y,
                                            float w, float h) {
        super(/*ce,*/ TYPE_RECT);
        value = new SVGOMRect(x, y, w, h);
    }

    /**
     * Create a new RectValue from a string.
     */
    public RectValue(/*ConstraintEngine ce,*/ String ss) {
        super(/*ce,*/ TYPE_RECT);
        value = parseRect(ss);
    }
    
    /**
     * Create a new RectValue from an SVGRect.
     */
    public RectValue(SVGRect r) {
        super(TYPE_RECT);
        value = new SVGOMRect(r.getX(),
                              r.getY(),
                              r.getWidth(),
                              r.getHeight());
    }
    
    /**
     * Get the rect value.
     */
    public SVGRect getRect() {
        return value;
    }

    /**
     * Parser for rects.
     */
    public class RectParser extends NumberParser {
        protected SVGRect rect = null;

        /**
         * Get the SVGRect that was just parsed.
         */
        public SVGRect getRect() {
            return rect;
        }

        /**
         * Parse a rect.
         */
        protected void doParse() throws ParseException, IOException {
            current = reader.read();
            skipSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();
            skipCommaSpaces();
            float w = parseFloat();
            skipCommaSpaces();
            float h = parseFloat();
            skipSpaces();
            rect = new SVGOMRect(x, y, w, h);
            if (current != -1) {
                reportError("end.of.stream.expected",
                        new Object[] { new Integer(current) });
            }
        }
    }

    /**
     * Convert the given string to a rect.
     */
    public SVGRect parseRect(String value) throws TypeErrorException {
        try {
            RectParser rp = new RectParser();
            rp.parse(value);
            SVGRect r = rp.getRect();
            if (r != null) {
                return r;
            }
        } catch (ParseException pe) {
        }
        invalidStringConversion(value, TYPE_RECT);
        return null;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return Float.toString(value.getX())
            + "," + Float.toString(value.getY())
            + "," + Float.toString(value.getWidth())
            + "," + Float.toString(value.getHeight());
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_RECT:
                return new RectValue(/*constraintEngine,*/
                                       value.getX(),
                                       value.getY(),
                                       value.getWidth(),
                                       value.getHeight());
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
        if (v instanceof RectValue) {
            RectValue rv = (RectValue) v;
            return value.getX() == rv.value.getX()
                && value.getY() == rv.value.getY()
                && value.getWidth() == rv.value.getWidth()
                && value.getHeight() == rv.value.getHeight();
        }
        return false;
    }

    // Operators /////////////////////////////////////////////////////////////

    public XObject equals(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (equals(v)) {
                return XBoolean.S_TRUE;
            } else {
                return XBoolean.S_FALSE;
            }
        }
        return null;
    }

    public XObject equalsRev(XObject l) throws TransformerException {
        return equals(l);
    }
    
    public XObject notequals(XObject r) throws TransformerException {
        XObject res = equals(r);
        return res == null ? null
               : res == XBoolean.S_TRUE ? XBoolean.S_FALSE
               : XBoolean.S_TRUE;
    }

    public XObject notequalsRev(XObject l) throws TransformerException {
        return notequals(l);
    }
    
    public XObject plus(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (v instanceof RectValue) {
                RectValue rv = (RectValue) v;
                return new XConstraintObject(
                        new RectValue(/*constraintEngine,*/
                                        value.getX() + rv.value.getX(),
                                        value.getY() + rv.value.getY(),
                                        value.getWidth() + rv.value.getWidth(),
                                        value.getHeight() + rv.value.getHeight()));
            }
        }
        return null;
    }

    public XObject plusRev(XObject l) throws TransformerException {
        return plus(l);
    }
    
    public XObject minus(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (v instanceof RectValue) {
                RectValue rv = (RectValue) v;
                return new XConstraintObject(
                        new RectValue(/*constraintEngine,*/
                                        value.getX() - rv.value.getX(),
                                        value.getY() - rv.value.getY(),
                                        value.getWidth() - rv.value.getWidth(),
                                        value.getHeight() - rv.value.getHeight()));
            }
        }
        return null;
    }

    public XObject minusRev(XObject l) throws TransformerException {
        if (l.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ l);
            if (v instanceof RectValue) {
                RectValue rv = (RectValue) v;
                return new XConstraintObject(
                        new RectValue(/*constraintEngine,*/
                                       rv.value.getX() - value.getX(),
                                       rv.value.getY() - value.getY(),
                                       rv.value.getWidth() - value.getWidth(),
                                       rv.value.getHeight() - value.getHeight()));
            }
        }
        return null;
    }

    public XObject mult(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new RectValue(/*constraintEngine,*/
                                value.getX() * f,
                                value.getY() * f,
                                value.getWidth() * f,
                                value.getHeight() * f));
    }

    public XObject multRev(XObject l) throws TransformerException {
        return mult(l);
    }

    public XObject div(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new RectValue(/*constraintEngine,*/
                                value.getX() / f,
                                value.getY() / f,
                                value.getWidth() / f,
                                value.getHeight() / f));
    }

    public XObject boolean_() throws TransformerException {
        return XBoolean.S_TRUE;
    }
}
