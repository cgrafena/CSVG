package org.apache.batik.constraint;

import org.apache.batik.bridge.Bridge;

/**
 * Bridge class for the CSVG property element.
 */
public class PropertyElementBridge
    extends VariableElementBridge {

    /**
     * Create a new bridge for the CSVG property element.
     */
    public PropertyElementBridge() {
    }

    /**
     * Returns 'property'.
     */
    public String getLocalName() {
        return CSVG_PROPERTY_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new PropertyElementBridge();
    }
}
