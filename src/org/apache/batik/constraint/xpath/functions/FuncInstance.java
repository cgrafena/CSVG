package org.apache.batik.constraint.xpath.functions;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Vector;

import org.apache.batik.constraint.XPathException;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;

/**
 * Get the custom element which created the shadow tree the context node
 * is a part of.
 */
public class FuncInstance extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "instance";
    }

    /**
     * Get the instance element for the shadow tree containing
     * the specified node.
     */
    public static Element findInstanceElement(Node n) {
        while (n != null && !(n instanceof SVGOMCSSImportedElementRoot)) {
            n = n.getParentNode();
        }
        if (n == null) {
            return null;
        }
        return ((SVGOMCSSImportedElementRoot) n).getCSSParentElement();
    }
    
    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 0);
        normaliseArguments(argVec);
        Node n = xpathContext.getExpressionContext().getContextNode();
        n = findInstanceElement(n);
        if (n == null) {
            throw new XPathException("c:instance() not called from shadow tree.");
        }
        return new XNodeSetForDOM(n, xpathContext.getDTMManager());
    }
}
