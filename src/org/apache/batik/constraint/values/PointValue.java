package org.apache.batik.constraint.values;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPoint;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;
import org.apache.batik.parser.DefaultPointsHandler;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Represents a point value.
 */
public class PointValue extends Value {

    /**
     * The point value.
     */
    SVGPoint value;
    
    /**
     * Create a new PointValue.
     */
    public PointValue(/*ConstraintEngine ce,*/ float x, float y) {
        super(/*ce,*/ TYPE_POINT);
        value = new SVGOMPoint(x, y);
    }

    /**
     * Create a new PointValue from a string.
     */
    public PointValue(/*ConstraintEngine ce,*/ String ps) {
        super(/*ce,*/ TYPE_POINT);
        value = parsePoint(ps);
    }
    
    /**
     * Create a new PointValue from an SVGPoint.
     */
    public PointValue(/*ConstraintEngine ce,*/ SVGPoint p) {
        super(/*ce,*/ TYPE_POINT);
        value = new SVGOMPoint(p.getX(), p.getY());
    }
    
    /**
     * Get the point value.
     */
    public SVGPoint getPoint() {
        return value;
    }

    /**
     * Event handler for points parser.
     */
    protected class PointsHandler extends DefaultPointsHandler {
        public SVGPoint p;
        public boolean got = false;
        public void point(float x, float y) throws ParseException {
            if (got) {
                throw new ParseException(null);
            }
            p = new SVGOMPoint(x, y);
            got = true;
        }
    }

    /**
     * Convert the given string to a point.
     */
    public SVGPoint parsePoint(String value) throws TypeErrorException {
        try {
            PointsHandler ph = new PointsHandler();
            PointsParser pp = new PointsParser();
            pp.setPointsHandler(ph);
            pp.parse(value);
            if (!ph.got) {
                invalidStringConversion(value, TYPE_POINT);
            }
            return ph.p;
        } catch (ParseException pe) {
            invalidStringConversion(value, TYPE_POINT);
        }
        return null;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return Float.toString(value.getX())
            + "," + Float.toString(value.getY());
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_POINT:
                return new PointValue(/*constraintEngine,*/
                                      value.getX(), 
                                      value.getY());
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
        if (v instanceof PointValue) {
            PointValue pv = (PointValue) v;
            return value.getX() == pv.value.getX()
                && value.getY() == pv.value.getY();
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
            if (v instanceof PointValue) {
                PointValue pv = (PointValue) v;
                return new XConstraintObject(
                        new PointValue(/*constraintEngine,*/
                                       value.getX() + pv.value.getX(),
                                       value.getY() + pv.value.getY()));
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
            if (v instanceof PointValue) {
                PointValue pv = (PointValue) v;
                return new XConstraintObject(
                        new PointValue(/*constraintEngine,*/
                                       value.getX() - pv.value.getX(),
                                       value.getY() - pv.value.getY()));
            }
        }
        return null;
    }

    public XObject minusRev(XObject l) throws TransformerException {
        if (l.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ l);
            if (v instanceof PointValue) {
                PointValue pv = (PointValue) v;
                return new XConstraintObject(
                        new PointValue(/*constraintEngine,*/
                                       pv.value.getX() - value.getX(),
                                       pv.value.getY() - value.getY()));
            }
        }
        return null;
    }

    public XObject mult(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new PointValue(/*constraintEngine,*/
                               value.getX() * f,
                               value.getY() * f));
    }

    public XObject multRev(XObject l) throws TransformerException {
        return mult(l);
    }

    public XObject div(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new PointValue(/*constraintEngine,*/
                               value.getX() / f,
                               value.getY() / f));
    }

    public XObject boolean_() throws TransformerException {
        return XBoolean.S_TRUE;
    }
}
