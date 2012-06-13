package org.apache.batik.constraint;

import org.w3c.dom.Node;

import org.apache.batik.extension.ExtensionElement;
import org.apache.batik.dom.AbstractDocument;

import org.apache.batik.bridge.Bridge;

/**
 * This class implements the CSVG constraint element.
 */
public class ConstraintElement
    extends ExtensionElement
    implements Constants {

    /**
     * Create a new ConstraintElement object.
     */
    protected ConstraintElement() {
    }

    /**
     * Create a new ConstraintElement object.
     */
    public ConstraintElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return CSVG_CONSTRAINT_TAG;
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
        return new ConstraintElement();
    }

    protected Bridge bridge;
    public void setBridge(Bridge b) { bridge = b; }
    public Bridge getBridge() { return bridge; }
}
