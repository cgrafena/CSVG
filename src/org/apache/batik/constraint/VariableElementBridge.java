package org.apache.batik.constraint;

import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;

import org.apache.batik.anim.AnimationEvent;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.extension.ExtensionElement;

import java.util.Collections;

/**
 * Bridge class for the CSVG variable element.
 */
public class VariableElementBridge
    extends ConstraintElementBridge {

    /**
     * Create a new bridge for the CSVG variable element.
     */
    public VariableElementBridge() {
    }

    /**
     * Returns 'variable'.
     */
    public String getLocalName() {
        return CSVG_VARIABLE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new VariableElementBridge();
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
        createEnvironment(e);
        // System.out.println("Handling variable $" + e.getAttributeNS(null, "name") + " = " + e.getAttributeNS(null, "value"));
        ((ConstraintElement) e).setBridge(this);
        SVGOMDocument doc = (SVGOMDocument) ctx.getDocument();
        ConstraintEngine ce = doc.getConstraintEngine();
        Constraint c = 
                new Variable(ce, e,
                    e.getAttributeNS(null, CSVG_VALUE_ATTRIBUTE));
        ((VariableElement) e).setConstraint(c);
        ce.addConstraint(c);
    }

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals("value")) {
            String s = evt.getNewValue();
            VariableElement ve = (VariableElement) evt.getTarget();
            ve.getConstraint().setConstraint(s);
        }
    }

    protected java.util.HashMap thisEnvironment;

    protected void createEnvironment(Element e) {
        thisEnvironment = (java.util.HashMap) ((java.util.HashMap) previousEnvironment).clone();
        thisEnvironment.put(e.getAttributeNS(null, "name"), e);
        /*System.out.println("\nAt variable " + e.getAttributeNS(null, "name") + " environment is:");
        Object[] ks = thisEnvironment.keySet().toArray();
        for (int i = 0; i < ks.length; i++) {
            System.out.println("\t" + ((String) ks[i]) + " : " + thisEnvironment.get((String) ks[i]));
        }*/
    }

    public java.util.HashMap getThisEnvironment() {
        return thisEnvironment;
    }
}
