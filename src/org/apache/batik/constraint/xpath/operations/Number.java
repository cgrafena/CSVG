package org.apache.batik.constraint.xpath.operations;

import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.Operators;
import org.apache.xpath.objects.XObject;

/**
 * Cast to number operator.
 */
public class Number extends org.apache.xpath.operations.Number {
    public XObject operate(XObject right)
            throws javax.xml.transform.TransformerException {
        XObject result = null;
        right = Value.normaliseXObject(right);
        if (right.getType() == XObject.CLASS_UNKNOWN
                && right.object() instanceof Operators) {
            result = ((Operators) right.object()).number();
        }
        return result == null ? super.operate(right) : result;
    }
}
