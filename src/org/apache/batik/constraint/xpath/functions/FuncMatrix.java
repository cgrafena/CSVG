package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.MatrixValue;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMMatrix;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * SVGMatrix constuctor function.
 */
public class FuncMatrix extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "Matrix";
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
                            new MatrixValue(/*constraintEngine,*/
                                           ((XObject) argVec.elementAt(0)).str()));
                case 6:
                    return new XConstraintObject(
                            new MatrixValue(/*constraintEngine,*/
                                           (float) ((XObject) argVec.elementAt(0)).num(),
                                           (float) ((XObject) argVec.elementAt(1)).num(),
                                           (float) ((XObject) argVec.elementAt(2)).num(),
                                           (float) ((XObject) argVec.elementAt(3)).num(),
                                           (float) ((XObject) argVec.elementAt(4)).num(),
                                           (float) ((XObject) argVec.elementAt(5)).num()));
            }
            assertNumberOfArguments(argVec, new int[] { 1, 6 });
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
        return null;
    }
}
