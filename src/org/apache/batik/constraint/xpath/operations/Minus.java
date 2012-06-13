package org.apache.batik.constraint.xpath.operations;

import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.Operators;
import org.apache.xpath.objects.XObject;

/**
 * Minus operator.
 */
public class Minus extends org.apache.xpath.operations.Minus {
    public XObject operate(XObject left, XObject right)
            throws javax.xml.transform.TransformerException {
        XObject result = null;
        left = Value.normaliseXObject(left);
        right = Value.normaliseXObject(right);
        if (left.getType() == XObject.CLASS_UNKNOWN
                && left.object() instanceof Operators) {
            result = ((Operators) left.object()).minus(right);
        } else if (right.getType() == XObject.CLASS_UNKNOWN
                && right.object() instanceof Operators) {
            result = ((Operators) right.object()).minusRev(left);
        }
        return result == null ? super.operate(left, right) : result;
    }
}
