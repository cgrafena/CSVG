package org.apache.batik.anim;

import org.apache.batik.constraint.Constraint;

/**
 * An animation whose value comes from evaluating an expression.
 */
public class ConstraintAnimation extends Animation {

    /**
     * The Constraint this animation uses.
     */
    Constraint constraint;

    /**
     * Create a new ConstraintAnimation from a Constraint object.
     */
    public ConstraintAnimation(ConstraintsAnimationEngine ae, Constraint c) {
    	
        super(ae, c.getElement(), c.getAttributeQName(), c.getAttributeType());
        constraint = c;
    }

    /**
     * Get the current value of the constraint expression.
     */
    public String getCurrentValue() {
    	//System.out.println("getCurrentValue() of constraint: " + constraint);
        return constraint.getValue().toString();
    }

    /**
     * Return whether this animation is currently active (that is, will
     * definitely cause an update at the current time).
     * For constraint animations, true will be returned if the constraint
     * refers to the c:time() function.
     */
    public boolean isActive() {
        return constraint.usesTime();
    }

    /**
     * Get a string representation of this animation.
     */
    public String toString() {
        return constraint.toString();
    }
}
