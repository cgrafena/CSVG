package org.apache.batik.constraint.xpath.functions;

import org.apache.batik.dom.svg.AbstractSVGLength;

/**
 * Vertical SVGLength constuctor function.
 */
public class FuncLengthV extends FuncLength {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "LengthV";
    }

    /**
     * The type of length value this function will construct.
     */
    protected short getLengthType() {
        return AbstractSVGLength.VERTICAL_LENGTH;
    }
}
