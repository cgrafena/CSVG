package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.MatrixValue;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * Compute the inverse of the given Matrix.
 */
public class FuncInverse extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "inverse";
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
            if (o instanceof MatrixValue) {
                MatrixValue mv = (MatrixValue) o;
                return new XConstraintObject(mv.inverse());
            }
        }
        throw new XPathException("inverse must be given a Matrix.");
    }
}
