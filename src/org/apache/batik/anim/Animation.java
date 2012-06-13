package org.apache.batik.anim;

import java.util.Calendar;

import org.w3c.dom.Element;

/**
 * Abstract class from which all types of animations derive.
 *
 * @author <a href="mailto:cam@mcc.id.au">Cameron McCormack</a>
 * @version $Id: Animation.java,v 1.7 2004/02/04 05:19:11 clm Exp $
 */
public abstract class Animation {
    
    /**
     * The animation engine this animation is associated with.
     */
    protected ConstraintsAnimationEngine animationEngine;
    
    /**
     * The element whose attribute is being animated.
     */
    protected Element element;

    /**
     * The name of the attribute being animated.
     */
    protected String attributeName;

    /**
     * The type of attribute being animated (XML or CSS).
     */
    protected short attributeType;

    /**
     * The value returned by getValue on the last invocation.
     */
    protected String cachedValue;

    /**
     * Whether the last getValue call returned a different value.
     */
    protected boolean hadChanged;

    /**
     * Creates a new animation.
     * @param ae The animation engine.
     * @param e The element whose attribute is being animated.
     * @param an The name of the attribute being animated.
     * @param at The type of attribute being animated (XML or CSS).
     */
    public Animation(ConstraintsAnimationEngine ae,
                     Element e,
                     String an,
                     short at) {
        animationEngine = ae;
        element = e;
        attributeName = an;
        attributeType = at;
    }
    
    /**
     * Get the animation engine that owns this animation.
     */
    public ConstraintsAnimationEngine getAnimationEngine() {
        return animationEngine;
    }

    /**
     * Get the element whose attribute is being animated.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Get the name of the attribute being animated.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Get the type of attribute being animated (XML or CSS).
     */
    public short getAttributeType() {
        return attributeType;
    }
    
    /**
     * Get the current value of this animation, updating the
     * internal cached value if necessary.
     */
    public String getValue() {
        String v = getCurrentValue();
        hadChanged =
            !(v == null && cachedValue == null
                || v != null && v.equals(cachedValue));
        //System.out.println("getValue() currentValue=" + v + " cachedValue=" + cachedValue + " hasChanged: " + hadChanged);
        cachedValue = v;
        return cachedValue;
    }
    
    /**
     * Get the current value of the animated attribute.  Should return null if
     * the current time is outside the scope of this animation.
     */
    public abstract String getCurrentValue();

    /**
     * Return whether the value that would be returned by getValue has
     * changed since the last getValue call.
     */
    public boolean hasChanged() {
        return hadChanged;
    }
    
    /**
     * Return whether this animation is currently active (that is, will
     * definitely cause an update at the current time).
     */
    public abstract boolean isActive();

    /**
     * An empty array of doubles.
     */
    protected static double[] emptyTimes = new double[0];

    /**
     * Get an array of the absolute begin times this animation
     * will have.
     */
    public double[] getAbsoluteBeginTimes() {
        return emptyTimes;
    }

    /**
     * Get a string representation of this animation.
     */
    public String toString() {
        return "(animation)";
    }
}
