package org.apache.batik.constraint;

import org.w3c.dom.Node;

/**
 * Class that represents a dependency on a variable.
 */
public class VariableDependency extends Dependency {

    /**
     * The context node from which variable resolution is to be performed.
     */
    protected Node contextNode;

    /**
     * The namespace URI of the variable.
     */
    protected String namespaceURI;

    /**
     * The local name of the variable.
     */
    protected String localName;

    /**
     * Create a new VariableDependency.
     */
    public VariableDependency(Constraint c,
                              String ns,
                              String ln) {
        super(c);
        namespaceURI = ns == null ? "" : ns;
        localName = ln;
    }

    /**
     * Get the variable element this dependency is for.
     */
    public VariableElement getVariableElement() {
        VariableElement ve = 
            org.apache.batik.constraint.xpath.Variable.lookupVariable(constraint.getElement(), namespaceURI, localName);
        if (ve == null) {
            throw new ConstraintException("Cannot resolve variable $" +
                    (namespaceURI.equals("") ? localName : namespaceURI + ":" + localName));
        }
        return ve;
    }
}
