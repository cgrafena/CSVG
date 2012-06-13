package org.apache.batik.bridge;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.ExtensibleSVGDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMCustomElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.SVGConstants;

/**
 * Bridge to initialise extension defs.
 */
public class SVGExtensionDefsElementBridge
        extends    AbstractSVGBridge 
        implements GenericBridge {
    
    /**
     * Constructs a new bridge for the &lt;extensionDefs&gt; element.
     */
    public SVGExtensionDefsElementBridge() {}

    /**
     * Returns 'extensionDefs'.
     */
    public String getLocalName() {
        return "extensionDefs";
    }

    /**
     * Invoked to handle an <tt>Element</tt> for a given <tt>BridgeContext</tt>.
     * For example, see the <tt>SVGDescElementBridge</tt>.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     */
    public void handleElement(BridgeContext ctx, Element e) {
        ExtensibleSVGDOMImplementation di = 
            (ExtensibleSVGDOMImplementation) e.getOwnerDocument().getImplementation();
        String ns = e.getAttributeNS(null, "namespace");
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (!(n.getNodeType() == Node.ELEMENT_NODE
                    && n.getNamespaceURI().equals(SVGConstants.SVG_NAMESPACE_URI)
                    && n.getLocalName().equals("elementDef"))) {
                continue;
            }
            Element elementDef = (Element) n;
            String ln = elementDef.getAttributeNS(null, "name");
            for (Node m = elementDef.getFirstChild(); m != null; m = m.getNextSibling()) {
                if (!(m.getNodeType() == Node.ELEMENT_NODE
                        && m.getNamespaceURI().equals(SVGConstants.SVG_NAMESPACE_URI)
                        && m.getLocalName().equals("transformer"))) {
                    continue;
                }
                Element transformer = (Element) m;
                String type = transformer.getAttributeNS(null, "type");
                if (!type.equals("text/xsl")) {
                    continue;
                }
                String transformerURI = XLinkSupport.getXLinkHref(transformer);
                if (transformerURI.length() == 0) {
                    throw new BridgeException(ctx, transformer, ErrorConstants.ERR_ATTRIBUTE_MISSING,
                                              new Object[] { "xlink:href" });
                }
                Element xslt = ctx.getReferencedElement(transformer, transformerURI);
                ctx.putBridge(new SVGCustomElementBridge(ns, ln, xslt));
                di.registerCustomElementFactory
                    (ns,
                     ln,
                     new CustomElementFactory(ns, ln));
            }
        }
    }

    /**
     * Factory to create SVGOMCustomElement objects.
     */
    protected static class CustomElementFactory 
            implements SVGDOMImplementation.ElementFactory {

        protected String namespaceURI;
        protected String localName;

        /**
         * Creates a new ConstraintElementFactory for the given
         * element with a specified XSLT stylesheet.
         */
        public CustomElementFactory(String ns, String ln) {
            namespaceURI = ns;
            localName = ln;
        }
        
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMCustomElement(prefix, 
                                          (AbstractDocument) doc,
                                          namespaceURI,
                                          localName);
        }
    }
}
