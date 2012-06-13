package org.apache.batik.constraint.values;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMMatrix;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Represents a matrix value.
 */
public class MatrixValue extends Value {

    /**
     * The matrix value.
     */
    SVGMatrix value;
    
    /**
     * Create a new MatrixValue.
     */
    public MatrixValue(/*ConstraintEngine ce,*/ float a, float b,
                                            float c, float d, 
                                            float e, float f) {
        super(/*ce,*/ TYPE_MATRIX);
        value = new SVGOMMatrix(new AffineTransform(a, b, c, d, e, f));
    }

    /**
     * Create a new MatrixValue from a string.
     */
    public MatrixValue(/*ConstraintEngine ce,*/ String ms) {
        super(/*ce,*/ TYPE_MATRIX);
        value = parseMatrix(ms);
    }
    
    /**
     * Create a new MatrixValue from am SVGMatrix.
     */
    public MatrixValue(SVGMatrix m) {
        super(TYPE_MATRIX);
        value = new SVGOMMatrix(new AffineTransform(m.getA(),
                                                    m.getB(),
                                                    m.getC(),
                                                    m.getD(),
                                                    m.getE(),
                                                    m.getF()));
    }

    /**
     * Get the matrix value.
     */
    public SVGMatrix getMatrix() {
        return value;
    }

    /**
     * Parser for matrices.
     */
    public class MatrixParser extends NumberParser {
        protected SVGMatrix matrix = null;

        /**
         * Get the SVGMatrix that was just parsed.
         */
        public SVGMatrix getMatrix() {
            return matrix;
        }

        /**
         * Parse a matrix.
         */
        protected void doParse() throws ParseException, IOException {
            current = reader.read();
            skipSpaces();
            float a = parseFloat();
            skipCommaSpaces();
            float b = parseFloat();
            skipCommaSpaces();
            float c = parseFloat();
            skipCommaSpaces();
            float d = parseFloat();
            skipCommaSpaces();
            float e = parseFloat();
            skipCommaSpaces();
            float f = parseFloat();
            skipSpaces();
            matrix = new SVGOMMatrix(new AffineTransform(a, b, c, d, e, f));
            if (current != -1) {
                reportError("end.of.stream.expected",
                        new Object[] { new Integer(current) });
            }
        }
    }

    /**
     * Convert the given string to a matrix.
     */
    public SVGMatrix parseMatrix(String value) throws TypeErrorException {
        try {
            MatrixParser mp = new MatrixParser();
            mp.parse(value);
            SVGMatrix m = mp.getMatrix();
            if (m != null) {
                return m;
            }
        } catch (ParseException pe) {
        }
        invalidStringConversion(value, TYPE_MATRIX);
        return null;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return Float.toString(value.getA())
            + "," + Float.toString(value.getB())
            + "," + Float.toString(value.getC())
            + "," + Float.toString(value.getD())
            + "," + Float.toString(value.getE())
            + "," + Float.toString(value.getF());
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_MATRIX:
                return new MatrixValue(/*constraintEngine,*/
                                       value.getA(),
                                       value.getB(),
                                       value.getC(),
                                       value.getD(),
                                       value.getE(),
                                       value.getF());
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ toString());
            case TYPE_TRANSFORM_LIST:
                return new TransformListValue(value);
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof MatrixValue) {
            MatrixValue mv = (MatrixValue) v;
            boolean result = value.getA() == mv.value.getA()
                    && value.getB() == mv.value.getB()
                    && value.getC() == mv.value.getC()
                    && value.getD() == mv.value.getD()
                    && value.getE() == mv.value.getE()
                    && value.getF() == mv.value.getF();
        }
        return false;
    }
    
    /**
     * Return a new MatrixValue which is the inverse of this matrix.
     */
    public MatrixValue inverse() {
        return new MatrixValue(value.inverse());
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
            if (v instanceof MatrixValue) {
                MatrixValue mv = (MatrixValue) v;
                return new XConstraintObject(
                        new MatrixValue(/*constraintEngine,*/
                                        value.getA() + mv.value.getA(),
                                        value.getB() + mv.value.getB(),
                                        value.getC() + mv.value.getC(),
                                        value.getD() + mv.value.getD(),
                                        value.getE() + mv.value.getE(),
                                        value.getF() + mv.value.getF()));
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
            if (v instanceof MatrixValue) {
                MatrixValue mv = (MatrixValue) v;
                return new XConstraintObject(
                        new MatrixValue(/*constraintEngine,*/
                                        value.getA() - mv.value.getA(),
                                        value.getB() - mv.value.getB(),
                                        value.getC() - mv.value.getC(),
                                        value.getD() - mv.value.getD(),
                                        value.getE() - mv.value.getE(),
                                        value.getF() - mv.value.getF()));
            }
        }
        return null;
    }

    public XObject minusRev(XObject l) throws TransformerException {
        if (l.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ l);
            if (v instanceof MatrixValue) {
                MatrixValue mv = (MatrixValue) v;
                return new XConstraintObject(
                        new MatrixValue(/*constraintEngine,*/
                                       mv.value.getA() - value.getA(),
                                       mv.value.getB() - value.getB(),
                                       mv.value.getC() - value.getC(),
                                       mv.value.getD() - value.getD(),
                                       mv.value.getE() - value.getE(),
                                       mv.value.getF() - value.getF()));
            }
        }
        return null;
    }

    public XObject mult(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (v instanceof MatrixValue) {
                return new XConstraintObject(
                    value.multiply(((MatrixValue) v).getMatrix()));
            } else if (v instanceof PointValue) {
                SVGPoint p = ((PointValue) v).getPoint().matrixTransform(value);
                return new XConstraintObject(new PointValue(/*constraintEngine,*/
                                                            p));
            }
        }
        float f = (float) r.num();
        return new XConstraintObject(
                new MatrixValue(/*constraintEngine,*/
                                value.getA() * f,
                                value.getB() * f,
                                value.getC() * f,
                                value.getD() * f,
                                value.getE() * f,
                                value.getF() * f));
    }

    public XObject multRev(XObject l) throws TransformerException {
        return mult(l);
    }

    public XObject div(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new MatrixValue(/*constraintEngine,*/
                                value.getA() / f,
                                value.getB() / f,
                                value.getC() / f,
                                value.getD() / f,
                                value.getE() / f,
                                value.getF() / f));
    }

    public XObject boolean_() throws TransformerException {
        return XBoolean.S_TRUE;
    }
}
