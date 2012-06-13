package org.apache.batik.constraint;

import org.apache.batik.anim.AnimationEvent;
import org.apache.batik.anim.ConstraintsAnimationEngine;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the CSVG constraint element.
 */
public class ConstraintElementBridge
    extends AbstractSVGBridge
    implements Constants, GenericBridge, BridgeUpdateHandler {

    /**
     * Create a new bridge for the CSVG constraint element.
     */
    public ConstraintElementBridge() {
    }

    /**
     * Return the CSVG namespace URI.
     */
    public String getNamespaceURI() {
        return CSVG_NAMESPACE_URI;
    }

    /**
     * Returns 'constraint'.
     */
    public String getLocalName() {
        return CSVG_CONSTRAINT_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new ConstraintElementBridge();
    }

    /**
     * Invoked to handle an <tt>Element</tt> for a given <tt>BridgeContext</tt>.
     * For example, see the <tt>SVGDescElementBridge</tt>.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     */
    public void handleElement(BridgeContext ctx, Element e) {
        if (e.getOwnerDocument().getDocumentElement().getAttributeNS(CSVG_NAMESPACE_URI, "batikIgnoreConstraints").equals("true")) {
            return;
        }
        previousEnvironment = findPreviousEnvironment(e);
        //System.out.println("Handling constraint " + e.getAttributeNS(null, "attributeName") + " = " + e.getAttributeNS(null, "value"));
        ((ConstraintElement) e).setBridge(this);
        String ats = e.getAttributeNS(null, "attributeType");
        short at;
        if (ats.equals("") || ats.equals("auto")) {
            at = ConstraintsAnimationEngine.ATTRIBUTE_TYPE_AUTO;
        } else if (ats.equals("XML")) {
            at = ConstraintsAnimationEngine.ATTRIBUTE_TYPE_XML;
        } else if (ats.equals("CSS")) {
            at = ConstraintsAnimationEngine.ATTRIBUTE_TYPE_CSS;
        } else {
            throw new BridgeException(ctx, e, ErrorConstants.ERR_ATTRIBUTE_VALUE_MALFORMED );
        }
        SVGOMDocument doc = (SVGOMDocument) ctx.getDocument();
        ConstraintEngine ce = doc.getConstraintEngine();
        ce.addConstraint(
                new Constraint(ce, (Element) e.getParentNode(),
                    e.getAttributeNS(null, "attributeName"),
                    at,
                    e.getAttributeNS(null, CSVG_VALUE_ATTRIBUTE)));
    }

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals("value")) {
            String s = evt.getNewValue();
            Element e = (Element) evt.getTarget();
            String an = e.getAttributeNS(null, "attributeName");
            Element pe = (Element) e.getParentNode();
            SVGOMDocument doc = (SVGOMDocument) e.getOwnerDocument();
            ConstraintEngine ce = doc.getConstraintEngine();
            Constraint c = ce.getConstraint(pe, null, an);
            c.setConstraint(s);
        }
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeRemoved' is fired.
     */
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
    }

    /**
     * Invoked when an MutationEvent of type 'DOMCharacterDataModified' 
     * is fired.
     */
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    /**
     * Invoked when an CSSEngineEvent is fired.
     */
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
    }

    /**
     * Invoked when an AnimationEvent is fired.
     */
    public void handleAnimationEvent(AnimationEvent evt) {
    }

    /**
     * Disposes this BridgeUpdateHandler and releases all resources.
     */
    public void dispose() {
    }

    protected java.util.HashMap previousEnvironment;

    public java.util.HashMap getPreviousEnvironment() {
        return previousEnvironment;
    }

    protected java.util.HashMap findPreviousEnvironment(Element e) {
        // System.out.println("finding previous environment for " + e.getTagName() + " name=" + e.getAttributeNS(null, "attributeName") + e.getAttributeNS(null, "name"));
        Node n = e;
        while (n != null) {
            Node ps = n.getPreviousSibling();
            if (ps == null) {
                n = n.getParentNode();
                if (n instanceof SVGOMCSSImportedElementRoot) {
                    n = ((SVGOMCSSImportedElementRoot) n).getCSSParentElement();
                }
                if (n == null) {
                    break;
                }
            } else {
                n = ps;
            }
            /*if (n != null) {
                System.out.println("   n is " + n.getClass().getName());
                if (n instanceof Element) {
                    System.out.println("   n name=" + ((Element)n).getAttributeNS(null, "name"));
                }
            }*/
            if (n instanceof VariableElement) {
                VariableElement ve = (VariableElement) n;
                VariableElementBridge veb = (VariableElementBridge) ve.getBridge();
                return veb.getThisEnvironment();
            }
        }
        return new java.util.HashMap();
    }
    
	@Override
	public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
		// TODO Auto-generated method stub
		 System.out.println("in handleAnimatedAttributeChanged()");
		
	}

	@Override
	public void handleOtherAnimationChanged(String type) {
		// TODO Auto-generated method stub
		 System.out.println("in handleOtherAnimationChanged()");
		
	}
}
