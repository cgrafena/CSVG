package org.apache.batik.constraint;

import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

/**
 * Class which can resolve namespace prefixes from the document.
 */
public class PrefixResolver implements org.apache.xml.utils.PrefixResolver {
    
    /**
     * The context node used for resolving prefixes if one
     * isn't given in the getNamespaceForPrefix method call.
     */
    Node contextNode;

    /**
     * Create a new PrefixResolver.
     */
    public PrefixResolver(Node context) {
        contextNode = context;
    }

    /**
     * Get the namespace URI for the given prefix
     * at the context node given at the time of construction.
     */
    public String getNamespaceForPrefix(String prefix) {
        return getNamespaceForPrefix(prefix, contextNode);
    }

    /**
     * Get the namespace URI for the given prefix at the
     * given context node.
     */
    public String getNamespaceForPrefix(String prefix, Node context) {
        Node currentNode = context;
        while (currentNode != null) {
            if (currentNode instanceof Element) {
                Element currentElement = (Element) currentNode;
                Attr attr;
                if (prefix == null || prefix.equals("")) {
                    attr = currentElement.getAttributeNodeNS
                        (XMLSupport.XMLNS_NAMESPACE_URI, "xmlns");
                } else {
                    attr = currentElement.getAttributeNodeNS
                        (XMLSupport.XMLNS_NAMESPACE_URI, prefix);
                }
                if (attr != null) {
                    return attr.getValue();
                }
            }
            // xgx start
            if (currentNode instanceof SVGDocument) {
            	SVGDocument doc = (SVGDocument) currentNode;
                Attr attr;
                if (prefix == null || prefix.equals("")) {
                    attr = doc.getRootElement().getAttributeNodeNS
                        (XMLSupport.XMLNS_NAMESPACE_URI, "xmlns");
                } else {
                    attr = doc.getRootElement().getAttributeNodeNS
                        (null, "xmlns:c");
                }
                
                if (attr != null) {
                    return attr.getValue();
                }
            	
            }
            // xgx end
            if (currentNode instanceof SVGOMCSSImportedElementRoot) {
                currentNode =
                    ((SVGOMCSSImportedElementRoot) currentNode).getCSSParentElement();
            } else {
                currentNode = currentNode.getParentNode();
            }
        }
        return null;
    }

    /**
     * Get the xml:base for context node.
     */
    public String getBaseIdentifier() {
        return null;
    }

    /**
     * Return whether this PrefixResolver handles null namespaces.
     */
    public boolean handlesNullPrefixes() {
        return false;
    }
}
