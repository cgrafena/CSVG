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

/**
 * Class that represents a dependency on the current scale and translate
 * values of the document.
 */
public class ZoomAndPanDependency extends Dependency {

    /**
     * The root SVG element to watch for zoom and pan events.
     */
    protected Element element;

    /**
     * The event listener for onscroll and onzoom events.
     */
    protected ZNPEventListener znpEventListener;

    /**
     * Get the element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Event listener class for onscroll and onzoom events.
     */
    protected class ZNPEventListener implements EventListener {

        /**
         * Handle the event.
         */
        public void handleEvent(Event e) {
            constraint.markDirty();
            ConstraintAnimation ca = constraint.getConstraintAnimation();
            ca.getAnimationEngine().queuePendingAnimations(
                    new Animation[] { ca });
        }
    }

    /**
     * Create a new ZoomAndPanDependency.
     */
    public ZoomAndPanDependency(Constraint c, Element e) {
        super(c);
        element = e;

        znpEventListener = new ZNPEventListener();
        ((EventTarget) e).addEventListener("SVGScroll", znpEventListener, false);
        ((EventTarget) e).addEventListener("SVGZoom", znpEventListener, false);
    }
}
