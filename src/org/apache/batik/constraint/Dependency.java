package org.apache.batik.constraint;

/**
 * Abstract class for dependencies.
 */
public abstract class Dependency {

    /**
     * The Constraint object that has this Dependency.
     */
    protected Constraint constraint;

    /**
     * Create a new Dependency object.
     */
    Dependency(Constraint c) {
        constraint = c;
    }
}
