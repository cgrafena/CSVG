package org.apache.batik.constraint;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintAnimation;

import org.apache.xpath.compiler.OpCodes;
import org.apache.batik.util.SVGConstants;

/**
 * Class that represents a dependency on the bounding box of
 * a particular element.
 */
public class BboxDependency extends Dependency {

    /**
     * The element whose bounding box is to be monitored.
     */
    protected Element element;

    /**
     * The event listener for DOM mutations.
     */
    protected MutationEventListener mutationEventListener;

    /**
     * The PrefixResolver to be used for attribute prefix resolution.
     */
    protected PrefixResolver prefixResolver;

    /**
     * Get the element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Event listener class for DOM mutations.
     */
    protected class MutationEventListener implements EventListener {

        /**
         * Handle the event.
         */
        public void handleEvent(Event e) {
            MutationEvent me = (MutationEvent) e;
            // dumpMutationEvent(me);
            // System.out.println("bbox MutationEvent");
            if (matchEvent(me)) {
                // System.out.println("  match");
                // System.out.println("TIME1 " + System.currentTimeMillis());
                Node n = (Node) me.getTarget();
                constraint.markDirty();
                ConstraintAnimation ca = constraint.getConstraintAnimation();
                
                if (ca != null) {
                	ca.getAnimationEngine().queuePendingAnimations(
                            new Animation[] { ca });	
                }
                
            } else {
                // System.out.println("  no match");
            }
        }
    }

    protected void dumpMutationEvent(MutationEvent me) {
        System.out.println("Mutation event: (from " + element.getNodeName()+ " bbox listener)");
        switch (me.getEventPhase()) {
            case Event.CAPTURING_PHASE:
                System.out.println("  Capturing phase");
                break;
            case Event.AT_TARGET:
                System.out.println("  At target");
                break;
            case Event.BUBBLING_PHASE:
                System.out.println("  Bubbling phase");
                break;
        }
        System.out.println("  Type: " + me.getType());
        System.out.println("  Target: " + ((Node) me.getTarget()).getNodeName());
        System.out.println("  Current target: " + ((Node) me.getCurrentTarget()).getNodeName());
        System.out.println("  Bubbles: " + (me.getBubbles() ? "true" : "false"));
        System.out.println("  Cancelable: " + (me.getCancelable() ? "true" : "false"));
        switch (me.getAttrChange()) {
            case MutationEvent.MODIFICATION:
                System.out.println("  Attr change: modification");
                break;
            case MutationEvent.ADDITION:
                System.out.println("  Attr change: addition");
                break;
            case MutationEvent.REMOVAL:
                System.out.println("  Attr change: removal");
                break;
        }
        System.out.println("  Previous value: " + me.getPrevValue());
        System.out.println("  New value: " + me.getNewValue());
        System.out.println("  Attr name: " + me.getAttrName());
        Node rn = me.getRelatedNode();
        System.out.println("  Related node: " + (rn == null ? "null" : rn.getNodeName()));
    }
    
    protected boolean matchEvent(MutationEvent me) {
        String type = me.getType();
        Node target = (Node) me.getTarget();
        if (type.equals("DOMAttrModified")) {
            Attr attr = (Attr) me.getRelatedNode();
            if (/*target == element
                    &&*/ Constraint.matchNode(me.getRelatedNode(), OpCodes.NODENAME, "", "*", prefixResolver)) {
                // XXX doesn't check orSelf
                return true;
            }
        } else if (type.equals("DOMCharacterDataModified")) {
            Node parent = target.getParentNode();
            String ns = parent.getNamespaceURI();
            String ln = parent.getLocalName();
            if (parent.getNodeType() == Node.ELEMENT_NODE
                    && ns.equals(SVGConstants.SVG_NAMESPACE_URI)
                    && ((ln.equals(SVGConstants.SVG_TEXT_TAG)
                            || ln.equals(SVGConstants.SVG_TSPAN_TAG)
                            || ln.equals(SVGConstants.SVG_TREF_TAG)
                            || ln.equals(SVGConstants.SVG_ALT_GLYPH_TAG))
                        // handle char data modifications on custom elements
                        || (!ln.equals(SVGConstants.SVG_NAMESPACE_URI)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new BboxDependency.
     */
    public BboxDependency(Constraint c, Element e) {
        super(c);
        element = e;
        prefixResolver = new PrefixResolver(e);

        // System.out.println("BboxDependency adding listener to " + e.getNodeName());
        mutationEventListener = new MutationEventListener();
        ((EventTarget) e.getOwnerDocument()).addEventListener("DOMAttrModified", mutationEventListener, true);
        ((EventTarget) e.getOwnerDocument()).addEventListener("DOMCharacterDataModified", mutationEventListener, true);
    }
}
