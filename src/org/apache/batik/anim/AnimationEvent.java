package org.apache.batik.anim;

import java.util.EventObject;

import org.w3c.dom.Element;

/**
 * This class represents an event fired by an AnimationEngine.
 *
 * @author <a href="mailto:cam@mcc.id.au">Cameron McCormack</a>
 * @version $Id: AnimationEvent.java,v 1.2 2003/12/18 12:20:01 clm Exp $
 */
public class AnimationEvent extends EventObject {

    /**
     * The element whose attribute is being animated.
     */
    protected Element element;

    /**
     * The name of the attribute being animated.
     */
    String attributeName;

    /**
     * The type of attribute (CSS or XML) being animated.
     */
    int attributeType;

    /**
     * The updated value.
     */
    String newValue;

    /**
     * Creates a new AnimationEvent.
     * @param source The source of the event.
     * @param e The element whose attribute is being animated.
     * @param an The name of the attribute being animated.
     * @param at The type of attribute being animated (CSS or XML).
     * @param nv The new animated value for the attribute.
     */
    public AnimationEvent(ConstraintsAnimationEngine source, Element e, String an, int at, String nv) {
        super(source);
        element = e;
        attributeName = an;
        attributeType = at;
        newValue = nv;
        //System.out.println("construct new animation event with value: " + newValue);
    }

    /**
     * Returns the element whose attribute is being animated.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Returns the name of the attribute being animated.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Returns the type of the attribute being animated.
     */
    public int getAttributeType() {
        return attributeType;
    }

    /**
     * Returns the new animated value for the attribute.
     */
    public String getNewValue() {
        return newValue;
    }
}
