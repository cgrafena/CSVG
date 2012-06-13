package org.apache.batik.constraint.xpath.operations;

import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.Operators;
import org.apache.xpath.objects.XObject;

/**
 * Greater than or equal operator.
 */
public class Gte extends org.apache.xpath.operations.Gte {
    public XObject operate(XObject left, XObject right)
            throws javax.xml.transform.TransformerException {
        XObject result = null;
        left = Value.normaliseXObject(left);
        right = Value.normaliseXObject(right);
        if (left.getType() == XObject.CLASS_UNKNOWN
                && left.object() instanceof Operators) {
            result = ((Operators) left.object()).gte(right);
        } else if (right.getType() == XObject.CLASS_UNKNOWN
                && right.object() instanceof Operators) {
            result = ((Operators) right.object()).gteRev(left);
        }
        return result == null ? super.operate(left, right) : result;
    }
}
