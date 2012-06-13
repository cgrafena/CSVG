package org.apache.batik.constraint.values;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.AbstractSVGTransformList;
import org.apache.batik.dom.svg.SVGOMMatrix;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.apache.batik.parser.TransformListParser;
import org.apache.batik.parser.DefaultTransformListHandler;
import org.apache.batik.parser.ParseException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Represents a transform list value.
 */
public class TransformListValue extends Value {

    /**
     * The transform list value.
     */
    SVGTransformList value;
    
    /**
     * Create a new TransformLsitValue from a string.
     */
    public TransformListValue(String tls) {
        super(TYPE_TRANSFORM_LIST);
        value = parseTransformList(tls);
    }
    
    /**
     * Create a new TransformListValue from am SVGMatrix.
     */
    public TransformListValue(SVGMatrix m) {
        super(TYPE_TRANSFORM_LIST);
        value = new SVGOMTransformList();
        SVGTransform t = new SVGOMTransform();
        t.setMatrix(m);
        value.appendItem(t);
    }

    /**
     * Create a new TransformListValue from am SVGTransformList.
     */
    public TransformListValue(SVGTransformList tl) {
        super(TYPE_TRANSFORM_LIST);
        value = new SVGOMTransformList();
        if (tl.getNumberOfItems() > 0) {
            SVGTransform t = new SVGOMTransform();
            t.setMatrix(tl.consolidate().getMatrix());
            value.appendItem(t);
        }
    }

    /**
     * Get the transform list value.
     */
    public SVGTransformList getTransformList() {
        return value;
    }

    public static class SVGOMTransformList extends AbstractSVGTransformList {
        protected String stringValue = "";
        
        protected DOMException createDOMException(short type, String key, Object[] args) { return null; }
        protected SVGException createSVGException(short type, String key, Object[] args) { return null; }
        protected String getValueAsString() { return stringValue; }
        protected void setAttributeValue(String value) { stringValue = value; }
    }
    
    /**
     * Handler for transform list parser.
     */
    public class TransformListHandler extends DefaultTransformListHandler {
        SVGOMTransformList transformList = null;
        
        public SVGTransformList getTransformList() {
            return transformList;
        }

        public void startTransformList() throws ParseException {
            transformList = new SVGOMTransformList();
        }
        
        public void matrix(float a, float b, float c, float d, float e, float f)
                throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setMatrix(new SVGOMMatrix(new AffineTransform(a, b, c, d, e, f)));
            transformList.appendItem(t);
        }

        public void rotate(float theta) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setRotate(theta, 0, 0);
            transformList.appendItem(t);
        }

        public void rotate(float theta, float cx, float cy) 
                throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setRotate(theta, cx, cy);
            transformList.appendItem(t);
        }

        public void translate(float tx) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setTranslate(tx, 0);
            transformList.appendItem(t);
        }

        public void translate(float tx, float ty) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setTranslate(tx, ty);
            transformList.appendItem(t);
        }

        public void scale(float sx) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setScale(sx, 0);
            transformList.appendItem(t);
        }

        public void scale(float sx, float sy) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setScale(sx, sy);
            transformList.appendItem(t);
        }

        public void skewX(float skx) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setSkewX(skx);
            transformList.appendItem(t);
        }

        public void skewY(float sky) throws ParseException {
            SVGTransform t = new SVGOMTransform();
            t.setSkewY(sky);
            transformList.appendItem(t);
        }
    }
    
    /**
     * Convert the given string to a transform list.
     */
    public SVGTransformList parseTransformList(String value)
            throws TypeErrorException {
        try {
            TransformListParser tlp = new TransformListParser();
            TransformListHandler tlh = new TransformListHandler();
            tlp.setTransformListHandler(tlh);
            tlp.parse(value);
            SVGTransformList tl = tlh.getTransformList();
            if (tl != null) {
                return tl;
            }
        } catch (ParseException pe) {
        }
        invalidStringConversion(value, TYPE_TRANSFORM_LIST);
        return null;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        SVGMatrix m = value.consolidate().getMatrix();
        return "matrix("
            + m.getA() + ","
            + m.getB() + ","
            + m.getC() + ","
            + m.getD() + ","
            + m.getE() + ","
            + m.getF() + ")";
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_TRANSFORM_LIST:
                return new TransformListValue(value);
            case TYPE_STRING:
                return new StringValue(toString());
            case TYPE_MATRIX:
                return new MatrixValue(value.consolidate().getMatrix());
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof TransformListValue) {
            SVGTransform t1 = value.consolidate();
            SVGTransform t2 = ((TransformListValue) v).getTransformList().consolidate();
            SVGMatrix m1 = t1.getMatrix();
            SVGMatrix m2 = t2.getMatrix();
            return (m1.getA() == m2.getA()
                    && m1.getB() == m2.getB()
                    && m1.getC() == m2.getC()
                    && m1.getD() == m2.getD()
                    && m1.getE() == m2.getE()
                    && m1.getF() == m2.getF());
        }
        return false;
    }
    
    // Operators /////////////////////////////////////////////////////////////

    public XObject equals(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(r);
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
    
    public XObject boolean_() throws TransformerException {
        return XBoolean.S_TRUE;
    }
}
