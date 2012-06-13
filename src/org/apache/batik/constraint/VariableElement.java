package org.apache.batik.constraint;

import org.w3c.dom.Node;

import org.apache.batik.extension.ExtensionElement;
import org.apache.batik.dom.AbstractDocument;

/**
 * This class implements the CSVG variable element.
 */
public class VariableElement
        extends ConstraintElement {

    /**
     * The variable constraint associated with this variable element.
     */
    Constraint variableConstraint;

    /**
     * Create a new VariableElement object.
     */
    protected VariableElement() {
    }

    /**
     * Create a new VariableElement object.
     */
    public VariableElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return CSVG_VARIABLE_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new VariableElement();
    }

    /**
     * Set the variable constraint associated with this variable element.
     */
    public void setConstraint(Constraint c) {
        variableConstraint = c;
    }

    /**
     * Get the variable constraint associated with this variable element.
     */
    public Constraint getConstraint() {
        return variableConstraint;
    }
}
