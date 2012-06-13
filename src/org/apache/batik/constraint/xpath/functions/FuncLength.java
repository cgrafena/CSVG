package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;
 
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.values.LengthValue;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.AbstractElement;
import org.apache.batik.dom.svg.AbstractSVGLength;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SVGLength constuctor function.
 */
public class FuncLength extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "Length";
    }

    /**
     * The type of length value this function will construct.
     */
    protected short getLengthType() {
        return AbstractSVGLength.OTHER_LENGTH;
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, new int[] { 1, 2 });
        normaliseArguments(argVec);
        XObject arg = (XObject) argVec.elementAt(0);
        Node contextNode = null;
        if (argVec.size() == 2) {
            XObject xo = (XObject) argVec.elementAt(1);
            try {
                if (xo.getType() == XObject.CLASS_NODESET) {
                    NodeList nl = xo.nodelist();
                    int l = nl.getLength();
                    if (l != 0) {
                        contextNode = nl.item(0);
                    } else {
                        throw new XPathException("Argument 2 of " + getFunctionName() + " must be a nodelist with at least one node.");
                    }
                }
            } catch (javax.xml.transform.TransformerException te) {
                throw new XPathException(te);
            }
        } else {
            contextNode = xpathContext.getExpressionContext().getContextNode();
        }
        return new XConstraintObject(
                new LengthValue(contextNode, arg.str(), getLengthType()));
    }
}
