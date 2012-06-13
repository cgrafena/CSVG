package org.apache.batik.constraint.xpath.functions;

import java.util.Vector;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.constraint.values.RectValue;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Get the viewport.
 */
public class FuncViewport extends ConstraintFunction {

    /**
     * Get the name of this function.
     */
    public String getFunctionName() {
        return "viewport";
    }

    /**
     * Execute this extension function.
     */
    public XObject execute(XPathContext xpathContext, Vector argVec) {
        assertNumberOfArguments(argVec, 0);
        Node n = xpathContext.getExpressionContext().getContextNode();
        SVGDocument d;
        if (n instanceof SVGDocument) {
            d = (SVGDocument) n;
        } else {
            d = (SVGDocument) n.getOwnerDocument();
        }
        SVGSVGElement svg = d.getRootElement();
                
        //return new XConstraintObject(new RectValue(svg.getViewport()));
        //UserAgent ua = new UserAgentAdapter();
       //return new XConstraintObject(new RectValue(0, 0, (float)ua.getViewportSize().getWidth(), (float)ua.getViewportSize().getHeight()));
        return new XConstraintObject(new RectValue(0, 0, 500, 420)); // xgx: svg.getViewport() never return and blocks GUI??
    }
}
