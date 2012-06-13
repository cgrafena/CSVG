package org.apache.batik.constraint;

import org.w3c.dom.Node;

import org.apache.batik.extension.PrefixableStylableExtensionElement;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.constraint.Constraint;

/**
 * This class implements the CSVG tval element.
 */
public class TValElement
        extends PrefixableStylableExtensionElement
        implements Constants {

    /**
     * The constraint associated with this tval element.
     */
    Constraint constraint;

    /**
     * Create a new TValElement object.
     */
    protected TValElement() {
    }

    /**
     * Create a new TValElement object.
     */
    public TValElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return CSVG_TVAL_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return CSVG_NAMESPACE_URI;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new TValElement();
    }

    /**
     * Set the constraint associated with this tval element.
     */
    public void setConstraint(Constraint c) {
        constraint = c;
    }

    /**
     * Get the constraint associated with this tval element.
     */
    public Constraint getConstraint() {
        return constraint;
    }
}
