package org.apache.batik.constraint.xpath.functions;

import org.apache.batik.dom.svg.AbstractSVGLength;

/**
 * Horizontal SVGLength constuctor function.
 */
public class FuncLengthH extends FuncLength {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "LengthH";
    }

    /**
     * The type of length value this function will construct.
     */
    protected short getLengthType() {
        return AbstractSVGLength.HORIZONTAL_LENGTH;
    }
}
