package org.apache.batik.constraint.xpath.functions;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGTextElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGLength;

import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.values.RectValue;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import org.apache.batik.dom.svg.SVGOMElement;

/**
 * Get the bounding box of a given SVG graphics element.
 */
public class FuncBbox extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "bbox";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 1);
        normaliseArguments(argVec);
        XObject xo = (XObject) argVec.firstElement();
        try {
            if (xo.getType() == XObject.CLASS_NODESET) {
                NodeList nl = xo.nodelist();
                int l = nl.getLength();
                if (l != 0) {
                    Node n = nl.item(0);
                    /*if (n instanceof SVGTextElement) {
                        SVGTextElement t = (SVGTextElement) n;
                        String s = t.getFirstChild().getNodeValue();
                        if (s == null) {
                            s = "";
                        }
                        float em = 18f; //((SVGOMElement) t).getSVGContext().getFontSize();
                        final float w = 0.5f * em * s.length();
                        final float h = 0.75f * em;
                        final float x = ((SVGLength) t.getX().getBaseVal().getItem(0)).getValue();
                        final float y = ((SVGLength) t.getY().getBaseVal().getItem(0)).getValue() - h;
                        SVGRect r = new SVGRect() {
                            public float getX() { return x; }
                            public float getY() { return y; }
                            public float getWidth() { return w; }
                            public float getHeight() { return h; }
                            public void setX(float x) { }
                            public void setY(float y) { }
                            public void setWidth(float w) { }
                            public void setHeight(float h) { }
                        };
                        return new XConstraintObject(new RectValue(r));
                    } else*/ if (n instanceof SVGLocatable) {
                        try {
                            SVGRect r = ((SVGLocatable) n).getBBox();
                            XObject res = new XConstraintObject(new RectValue(r));
                            return res;
                        } catch (NullPointerException npe) {
                            System.err.println("NPE thrown in getBBox");
                            return new XConstraintObject(new RectValue(0, 0, 0, 0));
                        }
                    }
                }
            }
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
        throw new XPathException("bbox must be given a nodelist with at least one SVG graphics element.");
    }
}
