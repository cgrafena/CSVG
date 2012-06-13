package org.apache.batik.constraint.xpath.operations;

import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.Operators;
import org.apache.xpath.objects.XObject;

/**
 * Equals operator.
 */
public class Equals extends org.apache.xpath.operations.Equals {
    public XObject operate(XObject left, XObject right)
            throws javax.xml.transform.TransformerException {
        XObject result = null;
        left = Value.normaliseXObject(left);
        right = Value.normaliseXObject(right);
        if (left.getType() == XObject.CLASS_UNKNOWN
                && left.object() instanceof Operators) {
            result = ((Operators) left.object()).equals(right);
        } else if (right.getType() == XObject.CLASS_UNKNOWN
                && right.object() instanceof Operators) {
            result = ((Operators) right.object()).equalsRev(left);
        }
        return result == null ? super.operate(left, right) : result;
    }
}
