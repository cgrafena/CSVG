package org.apache.batik.constraint;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Map;

import org.apache.batik.anim.AnimationEvent;
import org.apache.batik.anim.ConstraintsAnimationEngine;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CursorManager;
import org.apache.batik.bridge.SVGAElementBridge;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGTextPathElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.SVGAElementBridge.CursorHolder;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.text.TextPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the SVG text element.  This bridge extends the standard
 * SVG text element bridge to add support for the CSVG tval element.
 */
public class TextElementBridge
    extends SVGTextElementBridge
    implements Constants {

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new TextElementBridge();
    }

    /**
     * Add to the element children of the node, a
     * <code>SVGContext</code> to support dynamic updated . This is
     * recurssive, the children of the nodes are also traversed to add
     * to the support elements their context
     *
     * @param ctx a <code>BridgeContext</code> value
     * @param e an <code>Element</code> value
     *
     * @see org.apache.batik.dom.svg.SVGContext
     * @see org.apache.batik.bridge.BridgeUpdateHandler
     */
    protected void addContextToChild(BridgeContext ctx, Element e) {
        if (e.getNamespaceURI().equals(CSVG_NAMESPACE_URI)
                && e.getLocalName().equals(CSVG_TVAL_TAG)) {
            if (!e.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
                ((SVGOMElement) e).setSVGContext
                    (new TValBridge(ctx, this, e));
                SVGOMDocument doc = (SVGOMDocument) ctx.getDocument();
                ConstraintEngine ce = doc.getConstraintEngine();
                String expression = e.getAttributeNS(null, "value");
                Constraint c = new Constraint(ce, e, "value",
                                              ConstraintsAnimationEngine.ATTRIBUTE_TYPE_XML,
                                              e.getAttributeNS(null, "value"));
                ((TValElement) e).setConstraint(c);
                ce.addConstraint(c);
            }
        }
        super.addContextToChild(ctx, e);
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        Node childNode = (Node) evt.getTarget();
        
        if (childNode.getNodeType() == Node.ELEMENT_NODE
                && childNode.getNamespaceURI().equals(CSVG_NAMESPACE_URI)
                && childNode.getLocalName().equals(CSVG_TVAL_TAG)
                && !childNode.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
            addContextToChild(ctx, (Element) childNode);
            laidoutText = null;
        }
        super.handleDOMNodeInsertedEvent(evt);
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeRemoved' is fired.
     */
    public void handleDOMChildNodeRemovedEvent(MutationEvent evt) {
        Node childNode = (Node) evt.getTarget();
        
        if (childNode.getNodeType() == Node.ELEMENT_NODE
                && childNode.getNamespaceURI().equals(CSVG_NAMESPACE_URI)
                && childNode.getLocalName().equals(CSVG_TVAL_TAG)
                && !childNode.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
            laidoutText = null;
        }
        super.handleDOMChildNodeRemovedEvent(evt);
    }

    /**
     * Indicate of the parent of a node is
     * a displayed element.
     * &lt;title&gt;, &lt;desc&gt; and &lt;metadata&gt;
     * are non displayable elements.
     *
     * @return true if the parent of the node is &lt;text&gt;, 
     *   &lt;tspan&gt;, &lt;tref&gt;, &lt;textPath&gt;, &lt;a&gt;,
     *   &lt;altGlyph&gt;
     */
    protected boolean isParentDisplayed(Node childNode) {
        Node parentNode = childNode.getParentNode();
        if (parentNode.getNodeType() == Node.ELEMENT_NODE
                && parentNode.getNamespaceURI().equals(CSVG_NAMESPACE_URI)
                && parentNode.getLocalName().equals(CSVG_TVAL_TAG)
                && !parentNode.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
            return true;
        }
        return super.isParentDisplayed(childNode);
    }

    protected void addChildNullPaintAttributes(AttributedString as,
                                               Element element,
                                               BridgeContext ctx) {
        // Add Paint attributres for children of text element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!SVG_NAMESPACE_URI.equals(child.getNamespaceURI())) {
                continue;
            }
            String ln = child.getLocalName();
            if (ln.equals(SVG_TSPAN_TAG) ||
                ln.equals(SVG_ALT_GLYPH_TAG) ||
                ln.equals(SVG_A_TAG) ||
                ln.equals(SVG_TEXT_PATH_TAG) ||
                ln.equals(SVG_TREF_TAG) ||
                ln.equals(CSVG_TVAL_TAG) && !child.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
                Element childElement = (Element)child;
                addNullPaintAttributes(as, childElement, ctx);
            }
        }
    }

    /**
     * Fills the given AttributedStringBuffer.
     */
    protected void fillAttributedStringBuffer(BridgeContext ctx,
                                              Element element,
                                              boolean top,
                                              TextPath textPath,
                                              Integer bidiLevel,
                                              AttributedStringBuffer asb) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return;
        }
        
        String s = XMLSupport.getXMLSpace(element);
        boolean preserve = s.equals(SVG_PRESERVE_VALUE);
        boolean first = true;
        boolean last;
        boolean stripFirst  = !preserve;
        boolean stripLast   = !preserve;
        Element nodeElement = element;
        Map map = null;
        getAttributeMap(ctx, element, textPath, bidiLevel, map);
        Object o = map.get(TextAttribute.BIDI_EMBEDDING);
        Integer subBidiLevel = bidiLevel;
        if (o != null)
            subBidiLevel = ((Integer)o);

        for (Node n = element.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            
            last = n.getNextSibling() == null;
            
            int lastChar = asb.getLastChar();
            stripFirst = !preserve && first &&
                (top || lastChar == ' ' || lastChar == -1);
            
            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                nodeElement = (Element)n;
                String ns = n.getNamespaceURI();
                String ln = n.getLocalName();
                
                if (ns.equals(SVG_NAMESPACE_URI)) {
                    if (ln.equals(SVG_TSPAN_TAG) ||
                        ln.equals(SVG_ALT_GLYPH_TAG)) {
                        fillAttributedStringBuffer(ctx,
                                                   nodeElement,
                                                   false,
                                                   textPath,
                                                   subBidiLevel,
                                                   asb);
                    } else if (ln.equals(SVG_TEXT_PATH_TAG)) {
                        SVGTextPathElementBridge textPathBridge
                            = (SVGTextPathElementBridge)ctx.getBridge(nodeElement);
                        TextPath newTextPath
                            = textPathBridge.createTextPath(ctx, nodeElement);
                        if (newTextPath != null) {
                            fillAttributedStringBuffer(ctx,
                                                       nodeElement,
                                                       false,
                                                       newTextPath,
                                                       subBidiLevel,
                                                       asb);
                        }
                    } else if (ln.equals(SVG_TREF_TAG)) {
                        String uriStr = XLinkSupport.getXLinkHref((Element)n);
                        Element ref = ctx.getReferencedElement((Element)n, uriStr);
                        s = TextUtilities.getElementContent(ref);
                        s = normalizeString(s, preserve, stripFirst);
                        if (s != null) {
                            stripLast = !preserve && s.charAt(0) == ' ';
                            if (stripLast && !asb.isEmpty()) {
                                asb.stripLast();
                            }
                            Map m = null;
                            getAttributeMap(ctx, nodeElement, 
                                                    textPath, bidiLevel, m);
                            asb.append(s, m);
                        }
                    } else if (ln.equals(SVG_A_TAG)) {
                        EventTarget target = (EventTarget)nodeElement;
                        UserAgent ua = ctx.getUserAgent();
                        EventListener l = new SVGAElementBridge.AnchorListener(ua, new CursorHolder(CursorManager.DEFAULT_CURSOR));
                        target.addEventListener(SVG_EVENT_CLICK, l, false);
                        ctx.storeEventListener(target, SVG_EVENT_CLICK, l, false);
                        
                        fillAttributedStringBuffer(ctx,
                                                   nodeElement,
                                                   false,
                                                   textPath,
                                                   subBidiLevel,
                                                   asb);
                    }
                } else if (ns.equals(CSVG_NAMESPACE_URI)) {
                    if (ln.equals(CSVG_TVAL_TAG)) {
                        if (!e.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
                            Constraint c = ((TValElement) n).getConstraint();
                            String val = c.getValue().toString();
                            System.out.println("tval text: " + c + " == \"" + val + "\"");
                            Map m = null;
                            getAttributeMap(ctx, nodeElement, 
                                                    textPath, bidiLevel, m);
                            asb.append(val, m);
                        }
                    }
                }
                break;
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                s = n.getNodeValue();
                s = normalizeString(s, preserve, stripFirst);
                if (s != null) {
                    stripLast = !preserve && s.charAt(0) == ' ';
                    if (stripLast && !asb.isEmpty())
                        asb.stripLast();
                    asb.append(s, map);
                }
            }
            first = false;
        }
    }

    protected void addChildGlyphPositionAttributes(AttributedString as,
                                                   Element element,
                                                   BridgeContext ctx) {
        // do the same for each child element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!SVG_NAMESPACE_URI.equals(child.getNamespaceURI())) {
                continue;
            }
            String ln = child.getLocalName();
            if (ln.equals(SVG_TSPAN_TAG) ||
                ln.equals(SVG_ALT_GLYPH_TAG) ||
                ln.equals(SVG_A_TAG) ||
                ln.equals(SVG_TEXT_PATH_TAG) ||
                ln.equals(SVG_TREF_TAG) ||
                ln.equals(CSVG_TVAL_TAG) && !child.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
                addGlyphPositionAttributes(as, (Element)child, ctx);
            }
        }
    }

    protected void addChildPaintAttributes(AttributedString as,
                                           Element element,
                                           TextNode node,
                                           TextPaintInfo parentPI,
                                           BridgeContext ctx) {
        // Add Paint attributres for children of text element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!SVG_NAMESPACE_URI.equals(child.getNamespaceURI())) {
                continue;
            }
            String ln = child.getLocalName();
            if (ln.equals(SVG_TSPAN_TAG) ||
                ln.equals(SVG_ALT_GLYPH_TAG) ||
                ln.equals(SVG_A_TAG) ||
                ln.equals(SVG_TEXT_PATH_TAG) ||
                ln.equals(SVG_TREF_TAG) ||
                ln.equals(CSVG_TVAL_TAG) && child.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
                Element childElement = (Element)child;
                TextPaintInfo pi = getTextPaintInfo(childElement, node,
                                                        parentPI, ctx);
                addPaintAttributes(as, childElement, node, pi, ctx);
            }
        }
    }

    /**
     * BridgeUpdateHandle for &lt;c:tval&gt; element.
     */
    protected class TValBridge 
        extends AbstractTextChildTextContent {

        public TValBridge(BridgeContext ctx,
                          SVGTextElementBridge parent,
                          Element e) {
            super(ctx,parent,e);
        }

        /**
         * Handle the dynamic update for the attributes of 
         * &lt;c:tval&gt; : 'x', 'y', 'dx', 'dy' and 'rotate'.
         */
        public void handleDOMAttrModifiedEvent(MutationEvent evt){
            String attrName = evt.getAttrName();
            if (attrName.equals(SVG_X_ATTRIBUTE) ||
                attrName.equals(SVG_Y_ATTRIBUTE) ||
                attrName.equals(SVG_DX_ATTRIBUTE) ||
                attrName.equals(SVG_DY_ATTRIBUTE) ||
                attrName.equals(SVG_ROTATE_ATTRIBUTE)) {
                //recompute the layout of the text node
                recomputeLaidoutText();
            } else if (attrName.equals("value")) {
                String s = evt.getNewValue();
                TValElement e = (TValElement) evt.getTarget();
                e.getConstraint().setConstraint(s);
                recomputeLaidoutText();
            }
        }        

        /**
         * Invoked when an AnimationEngineEvent for an XML attribute is fired.
         */
        public void handleAnimationEvent(AnimationEvent evt) {
            TValElement e = (TValElement) evt.getElement();
            String an = evt.getAttributeName();
            if (an.equals(CSVG_VALUE_ATTRIBUTE)) {
                recomputeLaidoutText();
            }
        }
    }
}
