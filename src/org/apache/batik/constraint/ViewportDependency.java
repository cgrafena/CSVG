package org.apache.batik.constraint;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGSVGElement;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintAnimation;

import org.apache.xpath.compiler.OpCodes;

import org.apache.batik.constraint.xpath.functions.FuncViewport;

/**
 * Class that represents a dependency on the viewport.
 */
public class ViewportDependency extends Dependency {

    /**
     * The outermost SVG element.
     */
    SVGSVGElement svgElement;

    /**
     * The SVGResize event handler.
     */
    ResizeEventListener resizeEventListener;

    /**
     * Event listener class for document resizes.
     */
    protected class ResizeEventListener implements EventListener {

        /**
         * Handle the event.
         */
        public void handleEvent(Event e) {
            // System.out.println("Caught event: " + e.getType());
            constraint.markDirty();
            ConstraintAnimation ca = constraint.getConstraintAnimation();
            ca.getAnimationEngine().queuePendingAnimations(
                    new Animation[] { ca });
        }
    }

    /**
     * Create a new ViewportDependency.
     */
    public ViewportDependency(Constraint c, Node n) {
        super(c);
        svgElement = (SVGSVGElement) n.getOwnerDocument().getDocumentElement();

        // System.out.println("ViewportDependency adding listener to " + svgElement.getNodeName());
        resizeEventListener = new ResizeEventListener();
        ((EventTarget) svgElement).addEventListener("SVGResize", resizeEventListener, false);
    }
}
