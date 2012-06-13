package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.TypeErrorException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * If-then-else function.
 */
public class FuncIf extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "if";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 3);
        normaliseArguments(argVec);
        try {
            XObject xif = (XObject) argVec.elementAt(0);
            if (xif.bool()) {
                return (XObject) argVec.elementAt(1);
            }
            return (XObject) argVec.elementAt(2);
        } catch (javax.xml.transform.TransformerException te) {
            throw new TypeErrorException(te);
        }
    }
}
