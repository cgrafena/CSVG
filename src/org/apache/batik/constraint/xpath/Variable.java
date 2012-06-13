package org.apache.batik.constraint.xpath;

import org.w3c.dom.Node;

import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.ExpressionOrigin;
import org.apache.batik.constraint.PrefixResolver;
import org.apache.batik.constraint.VariableElement;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.apache.batik.constraint.ConstraintElementBridge;
import org.apache.batik.constraint.VariableElementBridge;
import org.apache.batik.constraint.ConstraintElement;
import org.apache.batik.constraint.VariableElement;
import org.w3c.dom.Element;

/**
 * Extension of the standard Xalan Variable class that performs
 * CSVG variable resolution.
 */
public class Variable extends org.apache.xpath.operations.Variable {

    /*protected static boolean matchVariable(VariableElement ve, String ns, String ln) {
        String nm = ve.getAttributeNS(null, "name");
        String vns;
        String prefix = DOMUtilities.getPrefix(nm);
        if (prefix == null) {
            vns = "";
        } else {
            PrefixResolver pr = new PrefixResolver(ve);
            vns = pr.getNamespaceForPrefix(prefix);
        }
        String vln = DOMUtilities.getLocalName(nm);
        return ns.equals(vns) && ln.equals(vln);
    }
    
    public static VariableElement lookupVariable(Node context, String ns, String ln) {
        Node n = context;
        // System.out.println("looking up variable " + ns + ":" + ln);
        while (n != null) {
            Node ps = n.getPreviousSibling();
            if (ps == null) {
                n = n.getParentNode();
                if (n instanceof SVGOMCSSImportedElementRoot) {
                    n = ((SVGOMCSSImportedElementRoot) n).getCSSParentElement();
                }
                if (n == null) {
                    break;
                }
            } else {
                n = ps;
            }
            // System.out.println("  checking against " + n.getNodeName());
            if (n instanceof VariableElement) {
                VariableElement ve = (VariableElement) n;
                if (matchVariable(ve, ns, ln)) {
                    // System.out.println("  matched");
                    return ve;
                }
            }
        }
        n = context.getNextSibling();
        while (n != null) {
            // System.out.println("  checking against " + n.getNodeName());
            if (n instanceof VariableElement) {
                VariableElement ve = (VariableElement) n;
                if (matchVariable(ve, ns, ln)) {
                    // System.out.println("  matched");
                    return ve;
                }
            }
            n = n.getNextSibling();
        }
        // System.out.println("  no match");
        return null;
    }*/

    public static VariableElement lookupVariable(Node context, String ns, String ln) {
        // long t1 = java.util.Calendar.getInstance().getTime().getTime();
        java.util.HashMap env = null;
        VariableElement ve = null;

        Node n = context.getPreviousSibling();
        if (n == null) {
            n = context.getParentNode();
        }
        // System.out.println(":: looking up " + ln);
        // System.out.println(":: n is " + n.getClass().getName());
        while (n != null) {
            if (n instanceof VariableElement) {
                // System.out.println(":: getting from variable element environment $" + ((Element)n).getAttributeNS(null, "name"));
                VariableElementBridge veb = (VariableElementBridge) ((VariableElement) n).getBridge();
                env = veb.getThisEnvironment();
                ve = (VariableElement) env.get(ln);
                break;
            } else if (n instanceof ConstraintElement) {
                // System.out.println(":: getting from constraint element environment " + ((Element)n).getAttributeNS(null, "attributeName"));
                // System.out.println("::     environment is:");
                ConstraintElementBridge ceb = (ConstraintElementBridge) ((ConstraintElement) n).getBridge();
                env = ceb.getPreviousEnvironment();
                /*Object[] ks = env.keySet().toArray();
                for (int i = 0; i < ks.length; i++) {
                    System.out.println("::         " + ((String) ks[i]) + " : " + env.get((String) ks[i]));
                }*/
                ve = (VariableElement) env.get(ln);
                break;
            }
            Node p = n.getPreviousSibling();
            if (p == null) {
                n = n.getParentNode();
            } else {
                n = p;
            }
        }

        if (ve == null) {
            // System.out.println(":: looking forward");
            Node m = context;
            while (m != null) {
                m = m.getNextSibling();
                // if (m != null) {
                //     System.out.println(":: m is " + m.getClass().getName());
                // }
                if (m != null
                        && "http://mcc.id.au/2004/csvg".equals(m.getNamespaceURI())
                        && "variable".equals(m.getLocalName())
                        && ln.equals(((Element) m).getAttributeNS(null, "name"))) {
                    ve = (VariableElement) m;
                    break;
                }
            }
        }
        // System.out.println(":: returning " + ve);
        // long t2 = java.util.Calendar.getInstance().getTime().getTime();
        // System.out.println("lookup: " + (t2 - t1));
        // System.out.println("== $" + ln + ": " + (t2 - t1) + "ms");
        return ve;
    }

    /**
     * Perform a variable lookup with a given context node.
     */
    public static XObject resolveVariable(Node n, String ns, String ln) {
        VariableElement ve = lookupVariable(n, ns, ln);
        if (ve == null) {
            return null;
        }
        Constraint c = ve.getConstraint();
        return new XConstraintObject(c.getValue());
    }

    public XObject execute(XPathContext xpathContext, boolean destructiveOK)
            throws javax.xml.transform.TransformerException {
        String ln = m_qname.getLocalName();
        if (ln == null) {
            ln = "";
        }
        // Object o = xpathContext.getOwnerObject();
        // Node n = ((ExpressionOrigin) o).getExpressionOrigin();
        int ni = xpathContext.getCurrentExpressionNode();
        Node n = xpathContext.getDTM(ni).getNode(ni);
        XObject xo = resolveVariable(n, "", ln);
        if (xo == null) {
            throw new javax.xml.transform.TransformerException(
                    "Cannot resolve variable $" + m_qname);
        }
        return xo;
    }

//     public XObject execute(XPathContext xpathContext, boolean destructiveOK)
//             throws javax.xml.transform.TransformerException {
// 
//         long t1 = java.util.Calendar.getInstance().getTime().getTime();
//         String ns = m_qname.getNamespaceURI();
//         if (ns == null) {
//             ns = "";
//         }
//         String ln = m_qname.getLocalName();
//         if (ln == null) {
//             ln = "";
//         }
// 
//         Object o = xpathContext.getOwnerObject();
//         // System.out.println("owner object is a " + o.getClass().getName());
//         Node n = ((ExpressionOrigin) o).getExpressionOrigin();
// 
//         XObject result = resolveVariable(n, ns, ln);
//         long t2 = java.util.Calendar.getInstance().getTime().getTime();
//         System.out.println("== $" + m_qname + ": " + (t2 - t1) + "ms");
//         if (result == null) {
//             throw new javax.xml.transform.TransformerException(
//                     "Cannot resolve variable $" + m_qname);
//         }
//         return result;
//     }
}
