package org.apache.batik.constraint.xpath.functions;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.util.Vector;

import org.apache.batik.constraint.values.PointValue;
import org.apache.batik.constraint.values.RectValue;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Get the 'y' attribute of the object.
 */
public class FuncY extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "y";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 1);
        normaliseArguments(argVec);
        XObject xo = (XObject) argVec.firstElement();
        if (xo.getType() == XObject.CLASS_UNKNOWN) {
            Object o = xo.object();
            if (o instanceof PointValue) {
                PointValue pv = (PointValue) o;
                return new XNumber(pv.getPoint().getY());
            } else if (o instanceof RectValue) {
                RectValue rv = (RectValue) o;
                return new XNumber(rv.getRect().getY());
            }
        }
        throw new TypeErrorException("Invalid type passed to function y.");
    }
}
