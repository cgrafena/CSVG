package org.apache.batik.constraint;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintAnimation;
import org.apache.xpath.compiler.OpCodes;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Class that represents a dependency on an attribute of a particular
 * element.
 */
public class AttributeDependency extends Dependency {

    /**
     * The node under which the attribute is to be monitored.
     */
    protected Node parentNode;

    /**
     * The namespace URI of the attribute to be monitored.
     */
    protected String namespaceURI;

    /**
     * The local name of the attribute to be monitored.
     */
    protected String localName;

    /**
     * Whether elements in the whole subtree under parentElement
     * should be monitored.
     */
    protected boolean recursive;

    /**
     * Whether the parent node should be matched also
     * in recursive cases.
     */
    protected boolean orSelf;
    
    /**
     * The PrefixResolver to be used for attribute prefix resolution.
     */
    protected PrefixResolver prefixResolver;

    /**
     * The event listener for DOM mutations.
     */
    protected MutationEventListener mutationEventListener;

    /**
     * Get the parent node of the attribute.
     */
    public Node getParentNode() {
        return parentNode;
    }

    /**
     * Get the namespace URI of the attribute.
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Get the local name of the attribute.
     */
    public String getLocalName() {
        return localName;
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
            if (matchEvent(me)) {
                // System.out.println("Matched event");
                // System.out.println("TIME1 " + System.currentTimeMillis());
                Node n = (Node) me.getTarget();
                constraint.markDirty();
                
                ConstraintAnimation ca = constraint.getConstraintAnimation();
                if (ca != null) {
                	ca.getAnimationEngine().queuePendingAnimations(
                            new Animation[] { ca });	
                }
                
                //initializeAnimation(ca, n, me);
                
                //ca.getAnimationEngine().addAnimation((AnimationTarget)me.getTarget(), AnimationEngine.ANIM_TYPE_XML, null, me.getAttrName(), ca); 
            }
        }
    }
    
    /**
     * Parses the animation element's target attributes and adds it to the
     * document's AnimationEngine.
     */
    /**protected void initializeAnimation(ConstraintAnimation ca, Node n, MutationEvent me) {
    	
        AbstractAnimation animation;        
        String attributeNamespaceURI;       
        String attributeLocalName;      
        short animationType;      
        SVGOMElement targetElement;       
        AnimationTarget animationTarget;   
        SVGOMElement element = (SVGOMElement)n;
       
        animationTarget = null;
        if (n instanceof SVGOMElement) {
            targetElement = (SVGOMElement) n;
            animationTarget = targetElement;
        }
        if (animationTarget == null) {
          
        	throw new Exception("animation target " + animationTarget + " not animatable");
        }

        // Get the attribute/property name.
        String an = element.getAttributeNS(null, SVGConstants.SVG_ATTRIBUTE_TYPE_ATTRIBUTE);
        int ci = an.indexOf(':');
        if (ci == -1) {
            if (element.hasProperty(an)) {
                animationType = AnimationEngine.ANIM_TYPE_CSS;
                attributeLocalName = an;
            } else {
                animationType = AnimationEngine.ANIM_TYPE_XML;
                attributeLocalName = an;
            }
        } else {
            animationType = AnimationEngine.ANIM_TYPE_XML;
            String prefix = an.substring(0, ci);
            attributeNamespaceURI = element.lookupNamespaceURI(prefix);
            attributeLocalName = an.substring(ci + 1);
        }
        if (animationType == AnimationEngine.ANIM_TYPE_CSS
                && !targetElement.isPropertyAnimatable(attributeLocalName)
            || animationType == AnimationEngine.ANIM_TYPE_XML
                && !targetElement.isAttributeAnimatable(attributeNamespaceURI,
                                                        attributeLocalName)) {
          
        	throw new Exception("attribute " + an + " not animatable");
        }

        // Check that the attribute/property is animatable with this
        // animation element.
        int type;
        if (animationType == AnimationEngine.ANIM_TYPE_CSS) {
            type = targetElement.getPropertyType(attributeLocalName);
        } else {
            type = targetElement.getAttributeType(attributeNamespaceURI,
                                                  attributeLocalName);
        }
        
        // Add the animation.
        TimedElement timedElement = new org.apache.batik.bridge.SVGAnimationElementBridge.SVGTimedElement();
        animation = new SimpleAnimation(timedElement, targetElement, ATTRIBUTE_TYPE_CSS, ); //createAnimation(animationTarget);
        ca.getAnimationEngine().addAnimation(animationTarget, animationType, attributeNamespaceURI,
                         attributeLocalName, animation);
    }**/

    protected void dumpMutationEvent(MutationEvent me) {
        System.out.println("Mutation event: (from " + localName + " listener)");
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
        Node target = (Node) me.getTarget();
        Attr attr = (Attr) me.getRelatedNode();
        if ((!recursive && target == parentNode || recursive)
                && Constraint.matchNode(me.getRelatedNode(), OpCodes.NODENAME, namespaceURI, localName, prefixResolver)) {
            // XXX doesn't check orSelf
            return true;
        }
        return false;
    }

    /**
     * Create a new AttributeDependency.
     * Can specify "*" for the local name to match all.
     */
    public AttributeDependency(Constraint c,
                               Node pn,
                               String ns,
                               String ln,
                               boolean rec,
                               boolean os) {
        super(c);
        parentNode = pn;
        namespaceURI = ns == null ? "" : ns;
        localName = ln;
        recursive = rec;
        orSelf = os;
        prefixResolver = new PrefixResolver(pn);

        // System.out.println("AttributeDependency adding listener to " + pn.getNodeName() + " for attribute " + ln);
        mutationEventListener = new MutationEventListener();
        EventTarget et;
        if (pn instanceof Document) {
            et = (EventTarget) pn;
        } else {
            et = (EventTarget) pn.getOwnerDocument();
        }
        et.addEventListener("DOMAttrModified", mutationEventListener, true);
        // ((EventTarget) pn).addEventListener("DOMSubtreeModified", mutationEventListener, true);
    }
}
