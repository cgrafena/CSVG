package org.apache.batik.constraint.xpath.functions;

import org.apache.batik.constraint.values.Value;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FuncId extends org.apache.xpath.functions.FuncId {
    public XObject execute(XPathContext xctxt) 
            throws javax.xml.transform.TransformerException {
    	
    	//System.out.println("Executing FuncID");
    	//Node n = xctxt.getExpressionContext().getContextNode();
    	Node n = xctxt.getExpressionContext().getContextNode();
    	
        Document doc = n.getOwnerDocument();
        XObject arg = Value.normaliseXObject(getArg0().execute(xctxt));
        // System.out.println("-- FuncId " + arg.str());
        Element e = doc.getElementById(arg.str());
        // System.out.println("   e == " + (e == null ? "null" : e.getNodeName()));
        XNodeSetForDOM nodes = new XNodeSetForDOM(e, xctxt.getDTMManager());
        return nodes;
        
    }
}
