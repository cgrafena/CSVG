package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.PropertyElement;
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.css.engine.CSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMCustomElement;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Get a property of the given custom element.
 */
public class FuncProperty extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "property";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 2);
        normaliseArguments(argVec);
        XObject xo = (XObject) argVec.firstElement();
        String pn = ((XObject) argVec.get(1)).toString();
        try {
            if (xo.getType() == XObject.CLASS_NODESET) {
                NodeList nl = xo.nodelist();
                // Constraint.dumpNodeList(nl);
                int l = nl.getLength();
                if (l != 0) {
                    Node n = nl.item(0);
                    if (n instanceof SVGOMCustomElement) {
                        CSSImportedElementRoot ier 
                            = ((SVGOMCustomElement) n).getCSSImportedElementRoot();
                        Node svg = ier.getFirstChild();
                        for (Node m = svg.getFirstChild(); m != null; m = m.getNextSibling()) {
                            if (m instanceof PropertyElement) {
                                String nm = ((PropertyElement) m).getAttributeNS(null, "name");
                                if (nm.equals(pn)) {
                                    Constraint c = ((PropertyElement) m).getConstraint();
                                    return new XConstraintObject(c.getValue());
                                }
                            }
                        }
                    }
                }
            }
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }
        throw new XPathException("c:property must be given a nodelist with at least one custom element.");
    }
}
