package org.apache.batik.constraint;

import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintAnimation;

/**
 * Class that represents a dependency on a node.
 */
public class NodeDependency extends Dependency {

    /**
     * The node under which nodes should be monitored.
     */
    protected Node parentNode;

    /**
     * The type of node test that should be applied to
     * potential nodes.  One of OpCodes.NODETYPE_* or
     * OpCodes.NODENAME.
     */
    protected int nodeTest;

    /**
     * The namespace URI of the node to be monitored.
     */
    protected String namespaceURI;

    /**
     * The local name of the node to be monitored.
     */
    protected String localName;

    /**
     * Whether nodes in the whole subtree under parentNode
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
                ca.getAnimationEngine().queuePendingAnimations(
                        new Animation[] { ca });
            }
        }
    }

    protected void dumpMutationEvent(MutationEvent me) {
        System.out.println("Mutation event:");
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
        if ((!recursive && target.getParentNode() == parentNode || recursive)
                && Constraint.matchNode(target, nodeTest, namespaceURI, localName, prefixResolver)) {
            // XXX doesn't check orSelf
            return true;
        }
        return false;
    }

    /**
     * Create a new NodeDependency.
     * Can specify "*" for the local name to match all, for
     * OpCodes.NODENAME tests.
     */
    public NodeDependency(Constraint c,
                          Node pn,
                          int nt,
                          String ns,
                          String ln,
                          boolean rec,
                          boolean os) {
        super(c);
        parentNode = pn;
        nodeTest = nt;
        namespaceURI = ns == null ? "" : ns;
        localName = ln;
        recursive = rec;
        orSelf = os;
        prefixResolver = new PrefixResolver(pn);

        // System.out.println("NodeDependency adding listener to " + pn.getNodeName());
        mutationEventListener = new MutationEventListener();
        // ((EventTarget) pn).addEventListener("DOMSubtreeModified", mutationEventListener, true);

        ((EventTarget) pn).addEventListener("DOMNodeInserted", mutationEventListener, true);
        ((EventTarget) pn).addEventListener("DOMNodeRemoved", mutationEventListener, true);
        ((EventTarget) pn).addEventListener("DOMCharacterDataModified", mutationEventListener, true);
    }
}
