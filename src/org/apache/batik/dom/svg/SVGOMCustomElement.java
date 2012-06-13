package org.apache.batik.dom.svg;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.batik.css.engine.CSSImportNode;
import org.apache.batik.css.engine.CSSImportedElementRoot;
import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTransformable;

/**
 * Custom element created via an extensionDefs.
 */
public class SVGOMCustomElement
    extends    SVGGraphicsElement
    implements CSSImportNode,
               SVGElement, SVGTests, SVGLangSpace, SVGStylable, SVGTransformable, EventTarget {

    /**
     * Store the imported element.
     */
    protected CSSImportedElementRoot cssImportedElementRoot;

    protected String namespaceURI;
    protected String localName;

    /**
     * Creates a new SVGOMCustomElement object.
     */
    protected SVGOMCustomElement() {
    }

    /**
     * Creates a new SVGOMCustomElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     * @param ns The namespace of the custom element.
     * @param ln The local name of the custom element.
     */
    public SVGOMCustomElement(String prefix,
                              AbstractDocument owner,
                              String ns,
                              String ln) {
        super(prefix, owner);
        namespaceURI = ns;
        localName = ln;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return localName;
    }

    // CSSImportNode //////////////////////////////////////////////////

    /**
     * The CSSImportedElementRoot.
     */
    public CSSImportedElementRoot getCSSImportedElementRoot() {
        return cssImportedElementRoot;
    }

    /**
     * Sets the CSSImportedElementRoot.
     */
    public void setCSSImportedElementRoot(CSSImportedElementRoot r) {
        if (cssImportedElementRoot != null) {
            for (Node n = cssImportedElementRoot.getFirstChild(); n != null; n = n.getNextSibling()) {
                nodeToBeRemoved(n);
            }
        }
        cssImportedElementRoot = r;
        // System.out.println("*** about to call nodeAdded, inDocumentTree == "
        //         + (inDocumentTree ? "true" : "false"));
        for (Node n = cssImportedElementRoot.getFirstChild(); n != null; n = n.getNextSibling()) {
            nodeAdded(n);
        }
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return null;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMCustomElement();
    }
}
