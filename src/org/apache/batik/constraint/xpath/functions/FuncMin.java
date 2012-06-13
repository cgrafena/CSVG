package org.apache.batik.constraint.xpath.functions;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.XPathException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Get the minimum of a set of values.
 */
public class FuncMin extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "min";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        normaliseArguments(argVec);
        float min = Float.POSITIVE_INFINITY;
        try {
            Iterator i = argVec.iterator();
            while (i.hasNext()) {
                XObject xo = (XObject) i.next();
                if (xo.getType() == XObject.CLASS_NODESET) {
                    DTMIterator nodes = xo.asIterator(xpathContext, xpathContext.getCurrentNode());
                    int pos;
                    while ((pos = nodes.nextNode()) != DTM.NULL) {
                        float f;
                        DTM dtm = nodes.getDTM(pos);
                        if (dtm.getNodeType(pos) == Node.ATTRIBUTE_NODE) {
                            Value v = Value.normaliseAttr((Attr) dtm.getNode(pos));
                            XObject num = v.number();
                            if (num == null) {
                                f = Float.parseFloat(v.toString());
                            } else {
                                f = (float) num.num();
                            }
                        } else {
                            XMLString s = dtm.getStringValue(pos);
                            f = (float) s.toDouble();
                        }
                        if (f < min) {
                            min = f;
                        }
                    }
                } else {
                    float f = (float) xo.num();
                    if (f < min) {
                        min = f;
                    }
                }
            }
            return new XNumber(min);
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
    }
}
