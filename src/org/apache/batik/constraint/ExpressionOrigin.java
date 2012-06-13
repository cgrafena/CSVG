package org.apache.batik.constraint;

import org.w3c.dom.Node;

public interface ExpressionOrigin {

    /**
     * Get the context of the whole expression being evaluated, for use as
     * a contenxt node for variable resoluvtion.
     */
    public Node getExpressionOrigin();
}
