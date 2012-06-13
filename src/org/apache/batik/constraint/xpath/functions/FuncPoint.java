package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.PointValue;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * SVGPoint constuctor function.
 */
public class FuncPoint extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "Point";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        normaliseArguments(argVec);
        try {
            switch (argVec.size()) {
                case 1:
                    return new XConstraintObject(
                            new PointValue(/*constraintEngine,*/
                                           ((XObject) argVec.elementAt(0)).str()));
                case 2:
                    return new XConstraintObject(
                            new PointValue(/*constraintEngine,*/
                                           (float) ((XObject) argVec.elementAt(0)).num(),
                                           (float) ((XObject) argVec.elementAt(1)).num()));
            }
            assertNumberOfArguments(argVec, new int[] { 1, 2 });
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
        return null;
    }
}
