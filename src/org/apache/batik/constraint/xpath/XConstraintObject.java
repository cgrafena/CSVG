package org.apache.batik.constraint.xpath;

import org.apache.batik.constraint.xpath.Operators;
import org.apache.xpath.objects.XObject;

/**
 * Specialisation of org.apache.xpath.objects.XObject that can handle
 * converting to numbers, booleans, strings.
 */
public class XConstraintObject extends XObject {

    /**
     * Create a new XConstraintObject.
     */
    public XConstraintObject(Object o) {
        super(o);
    }
    
    /**
     * Convert this object to a double.
     */
    public double num() throws javax.xml.transform.TransformerException {
        if (m_obj instanceof Operators) {
            XObject res = ((Operators) m_obj).number();
            if (res != null) {
                return res.num();
            }
        }
        return super.num();
    }

    /**
     * Convert this object to a boolean.
     */
    public boolean bool() throws javax.xml.transform.TransformerException {
        if (m_obj instanceof Operators) {
            XObject o = ((Operators) m_obj).boolean_();
            if (o != null) {
                return o.bool();
            }
        }
        return super.bool();
    }
}
