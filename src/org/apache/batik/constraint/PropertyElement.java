package org.apache.batik.constraint;

import org.w3c.dom.Node;

import org.apache.batik.dom.AbstractDocument;

/**
 * This class implements the CSVG property element.
 */
public class PropertyElement extends VariableElement {

    /**
     * Create a new PropertyElement object.
     */
    protected PropertyElement() {
    }

    /**
     * Create a new PropertyElement object.
     */
    public PropertyElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return CSVG_PROPERTY_TAG;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new PropertyElement();
    }
}
