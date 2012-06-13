package org.apache.batik.constraint.xpath.functions;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGMatrix;

import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.MatrixValue;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Get the current screen transformation matrix of a given SVG graphics
 * element.
 */
public class FuncScreenCTM extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "screenCTM";
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
                    if (n instanceof SVGLocatable) {
                        SVGMatrix m = ((SVGLocatable) n).getScreenCTM();
                        return new XConstraintObject(new MatrixValue(m));
                    }
                }
            }
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
        throw new XPathException("screenCTM must be given a nodelist with at least one SVG graphics element.");
    }
}
