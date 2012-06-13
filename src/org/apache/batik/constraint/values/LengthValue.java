package org.apache.batik.constraint.values;

import javax.xml.transform.TransformerException;

import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.AbstractElement;
import org.apache.batik.dom.svg.AbstractSVGLength;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.SVGOMLength;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLength;

/**
 * Represents a length value.
 */
public class LengthValue extends Value {

    /**
     * The length value.
     */
    SVGLength value;
    
    /**
     * The associated element (to determine percentages, etc.).
     */
    AbstractElement associatedElement;

    /**
     * Create a new LengthValue.
     */
    public LengthValue(/*ConstraintEngine ce,*/ Node n, float f) {
        super(/*ce,*/ TYPE_LENGTH);
        associatedElement = getAncestorSVGContextElement(n);
        value = parseLength(Float.toString(f));
    }

    /**
     * Create a new LengthValue.
     */
    public LengthValue(/*ConstraintEngine ce,*/ Node n, String s) {
        super(/*ce,*/ TYPE_LENGTH);
        associatedElement = getAncestorSVGContextElement(n);
        value = parseLength(s);
    }
    
    /**
     * Create a new LengthValue with a given direction.
     */
    public LengthValue(Node n, String s, short d) {
        super(TYPE_LENGTH);
        associatedElement = getAncestorSVGContextElement(n);
        value = parseLength(s, d);
    }
    
    /**
     * Gets the first ancestor element which has an SVGContext defined
     * for it (typically, an element whose bridge is an
     * AbstractGraphicsNodeBridge).
     */
    protected AbstractElement getAncestorSVGContextElement(Node n) {
        // System.out.println("Getting ancestor SVG context element for " + n);
        if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            n = ((Attr) n).getOwnerElement();
        }
        while (n != null) {
            if (n instanceof SVGOMElement) {
                if (((SVGOMElement) n).getSVGContext() != null) {
                    // System.out.println("\treturning " + n);
                    return (AbstractElement) n;
                }
            }
            n = n.getParentNode();
            if (n instanceof SVGOMCSSImportedElementRoot) {
                n = ((SVGOMCSSImportedElementRoot) n).getCSSParentElement();
            }
        }
        // System.out.println("\treturning null");
        return null;
    }
    
    /**
     * Get the length value.
     */
    public SVGLength getLength() {
        return value;
    }

    /**
     * Convert the given string to a length.
     */
    public SVGLength parseLength(String value) throws TypeErrorException {
        return parseLength(value, AbstractSVGLength.OTHER_LENGTH);
    }

    /**
     * Convert the given string to a length.
     */
    public SVGLength parseLength(String value, short d) throws TypeErrorException {
        // System.out.println("LengthValue.parseLength(\"" + value + "\"), assocatedElement = " + associatedElement);
        SVGOMLength l = new SVGOMLength(associatedElement);
        l.setValueAsString(value);
        // System.out.println("\tassociatedElement.getSVGContext().getViewportWidth() = " + ((SVGOMElement) associatedElement).getSVGContext().getViewportWidth());
        // System.out.println("\tl.getValue() == " + l.getValue());
        // System.out.println("\tfrom SVGLength: " + l);
        if (l.getUnitType() == SVGLength.SVG_LENGTHTYPE_UNKNOWN) {
            invalidStringConversion(value, TYPE_LENGTH);
        }
        return l;
    }

    /**
     * Get a string representation of this value.
     */
    public String toString() {
        return value.getValueAsString();
    }

    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type, Node origin) throws TypeErrorException {
        switch (type) {
            case TYPE_LENGTH:
                return new LengthValue(/*constraintEngine,*/ origin == null ? associatedElement : origin, toString());
            case TYPE_COORDINATE:
                return new CoordinateValue(/*constraintEngine,*/ origin == null ? associatedElement : origin, toString());
            case TYPE_STRING:
                return new StringValue(/*constraintEngine,*/ toString());
            case TYPE_FONT_SIZE_VALUE:
            case TYPE_KERNING_VALUE:
            case TYPE_SPACING_VALUE:
            case TYPE_LENGTH_OR_INHERIT:
                return new UnconvertibleValue(/*constraintEngine,*/ toString(), type);
        }
        invalidConversion(this.type, type);
        return null;
    }

    /**
     * Test if two Value objects are equivalent.
     */
    public boolean equals(Value v) {
        if (v instanceof LengthValue) {
            LengthValue lv = (LengthValue) v;
            return lv.value.getValue() == value.getValue();
        }
        return false;
    }
    
    // Operators /////////////////////////////////////////////////////////////

    public XObject equals(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (equals(v)) {
                return XBoolean.S_TRUE;
            }
            return XBoolean.S_FALSE;
        } else {
            if (value.getValue() == (float) r.num()) {
                return XBoolean.S_TRUE;
            }
            return XBoolean.S_FALSE;
        }
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
            if (v instanceof LengthValue) {
                LengthValue lv = (LengthValue) v;
                return new XConstraintObject(
                        new LengthValue(/*constraintEngine,*/
                                        associatedElement,
                                        value.getValue() + lv.getLength().getValue()));
            }
        } else {
            float f = (float) r.num();
            // System.out.println("lhs = " + value.getValue());
            // System.out.println("rhs = " + f);
            return new XConstraintObject(
                    new LengthValue(/*constraintEngine,*/
                                    associatedElement,
                                    value.getValue() + f));
        }
        return null;
    }

    public XObject plusRev(XObject l) throws TransformerException {
        return plus(l);
    }
    
    public XObject minus(XObject r) throws TransformerException {
        if (r.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ r);
            if (v instanceof LengthValue) {
                LengthValue lv = (LengthValue) v;
                return new XConstraintObject(
                        new LengthValue(/*constraintEngine,*/
                                        associatedElement,
                                        value.getValue() - lv.getLength().getValue()));
            }
        } else {
            float f = (float) r.num();
            return new XConstraintObject(
                    new LengthValue(/*constraintEngine,*/
                                    associatedElement,
                                    value.getValue() - f));
        }
        return null;
    }

    public XObject minusRev(XObject l) throws TransformerException {
        if (l.getType() == XObject.CLASS_UNKNOWN) {
            Value v = createValue(/*constraintEngine,*/ l);
            if (v instanceof LengthValue) {
                LengthValue lv = (LengthValue) v;
                return new XConstraintObject(
                        new LengthValue(/*constraintEngine,*/
                                        associatedElement,
                                        lv.getLength().getValue() - value.getValue()));
            }
        } else {
            float f = (float) l.num();
            return new XConstraintObject(
                    new LengthValue(/*constraintEngine,*/
                                    associatedElement,
                                    f - value.getValue()));
        }
        return null;
    }

    public XObject mult(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new LengthValue(/*constraintEngine,*/
                                associatedElement,
                                value.getValue() * f));
    }

    public XObject multRev(XObject l) throws TransformerException {
        return mult(l);
    }

    public XObject div(XObject r) throws TransformerException {
        float f = (float) r.num();
        return new XConstraintObject(
                new LengthValue(/*constraintEngine,*/
                                associatedElement,
                                value.getValue() / f));
    }

    public XObject number() throws TransformerException {
        return new XNumber(value.getValue());
    }

    public XObject boolean_() throws TransformerException {
        return value.getValue() != 0 ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
