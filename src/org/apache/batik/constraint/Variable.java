package org.apache.batik.constraint;

import org.apache.batik.anim.ConstraintsAnimationEngine;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * A variable in the document.
 */
public class Variable extends Constraint {

    /**
     * Create a new Variable object.
     */
    public Variable(ConstraintEngine cm,
                    Element e,
                    String expr) {
        super(cm, e, "value", ConstraintsAnimationEngine.ATTRIBUTE_TYPE_XML, expr);
    }

    /**
     * Get the name of this variable.
     */
    public String getName() {
        return element.getAttributeNS(null, SVGConstants.SVG_NAME_ATTRIBUTE);
    }

    /**
     * Turn this constraint into a printable string.
     */
    public String toString() {
        String id = element.getAttributeNS(null, "id");
        return "$" + getName() + (id.equals("") ? "" : "#" + id)
            + " = " + expression;
    }
}
