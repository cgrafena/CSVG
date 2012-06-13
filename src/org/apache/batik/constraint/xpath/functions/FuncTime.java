package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class FuncTime extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "time";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        // System.out.println("*** getting syncbase time = " + 
        //     constraintEngine.getAnimationEngine().getSyncbaseTime());
        return new XNumber
            (constraintEngine.getAnimationEngine().getSyncbaseTime());
    }
}
