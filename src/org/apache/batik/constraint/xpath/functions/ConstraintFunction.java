package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.ArgumentCountException;
import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.values.Value;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * This class is an abstract base class for all CSVG extension functions.
 */
public abstract class ConstraintFunction {

    /**
     * The ConstraintEngine the Values created by this
     * function will be owned by.
     */
    protected ConstraintEngine constraintEngine;

    /**
     * Execute this extension function.
     */
    public abstract XObject execute(XPathContext xpathContext, Vector argVec);

    /**
     * Get the name of this function.
     */
    public abstract String getFunctionName();

    /**
     * Set the ConstraintEngine the values created by this function
     * are owned by.
     */
    public void setConstraintEngine(ConstraintEngine ce) {
        constraintEngine = ce;
    }
    
    /**
     * Assert that this function was given a certain number of arguments.
     */
    protected void assertNumberOfArguments(Vector argVec, int n) {
        if (argVec.size() != n) {
            throw new ArgumentCountException(getFunctionName(),
                                             argVec.size(), 
                                             n);
        }
    }
    
    /**
     * Assert that this function was given a certain number of arguments.
     */
    protected void assertNumberOfArguments(Vector argVec, int[] ns) {
        int len = argVec.size();
        for (int i = 0; i < ns.length; i++) {
            if (ns[i] == len) {
                return;
            }
        }
        throw new ArgumentCountException(getFunctionName(),
                                         argVec.size(), 
                                         ns);
    }

    /**
     * Normalise the XObject arguments passed to the function.
     */
    protected void normaliseArguments(Vector argVec) {
        int len = argVec.size();
        for (int i = 0; i < len; i++) {
            XObject arg = (XObject) argVec.elementAt(i);
            argVec.setElementAt(Value.normaliseXObject(arg), i);
        }
    }
}
