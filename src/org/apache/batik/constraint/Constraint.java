package org.apache.batik.constraint;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintAnimation;
import org.apache.batik.constraint.values.BooleanValue;
import org.apache.batik.constraint.values.NumberValue;
import org.apache.batik.constraint.values.StringValue;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XPath;
import org.apache.batik.constraint.values.CoordinateValue;
import org.apache.batik.constraint.values.LengthValue;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;

import org.apache.xpath.XPathVisitor;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XNumber;

import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionMultiArgs;

import org.apache.xpath.axes.WalkingIterator;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.FilterExprWalker;
import org.apache.xpath.axes.PredicatedNodeTest;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.patterns.NodeTest;

import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.FuncId;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Set;
import org.apache.batik.constraint.xpath.functions.FuncViewport;
import org.apache.batik.constraint.xpath.functions.FuncInstance;

import org.apache.batik.util.SVGConstants;

/**
 * An expression associated with an XML attribute or CSS value of an element.
 */
public class Constraint implements ExpressionOrigin, SVGConstants {

    /**
     * The ConstraintEngine this Constraint object is associated with.
     */
    ConstraintEngine constraintEngine;

    /**
     * The element whose XML attribute or CSS property is being constrained.
     */
    Element element;

    /**
     * The QName of the attribute being constrained.
     */
    String attributeQName;

    /**
     * The local name of the XML attribute or CSS property being constrained.
     */
    String attributeLocalName;

    /**
     * The namespace of the attribute.
     */
    String attributeNamespaceURI;

    /**
     * The type of attribute being constrained (XML, CSS or auto).
     */
    short attributeType;
    
    /**
     * The expression string for this constraint.
     */
    String expression;

    /**
     * The parsed XPath expression.
     */
    XPath xpath;

    /**
     * The XPathContext used for evaluation.
     */
    // XPathContext xpathContext;

    /**
     * The PrefixResolver used for the XPath.
     */
    PrefixResolver prefixResolver;

    /**
     * The dirty bit.
     */
    boolean dirty;

    /**
     * The cached constraint value.
     */
    public Value cachedValue;

    /**
     * The ConstraintAnimation object which owns this Constraint.
     */
    ConstraintAnimation constraintAnimation;

    /**
     * The type that this constraint should be.
     * Set to TYPE_UNKNOWN for Variables.
     */
    int type;

    /**
     * List of dependencies for this constraint.
     */
    LinkedList dependencies = new LinkedList();
    
    /**
     * Whether this constraint refers to the c:time()
     * function.
     */
    protected boolean expressionUsesTime;

    protected static int nextNumber = 0;
    protected int number;

    /**
     * Create a new Constraint object.
     */
    public Constraint(ConstraintEngine ce,
                      Element e,
                      String an,
                      short at,
                      String expr) {
        number = nextNumber++;
        constraintEngine = ce;
        element = e;
        attributeQName = an;
        attributeType = at;
        //xpathContext = new XPathContext(this);
        prefixResolver = new PrefixResolver(e);
        String prefix = DOMUtilities.getPrefix(an);
        if (prefix == null) {
            attributeNamespaceURI = "";
        } else {
            attributeNamespaceURI = prefixResolver.getNamespaceForPrefix(prefix);
        }
        attributeLocalName = DOMUtilities.getLocalName(an);
        type = Value.getAttributeType(e.getNamespaceURI(),
                                      e.getLocalName(),
                                      attributeNamespaceURI,
                                      attributeLocalName);
        setConstraint(expr);
    }

    /**
     * Get the constraint's expression string.
     */
    public String getExpression() {
        return expression;
    }

    boolean pretend = false;

    /**
     * Give this constraint a new expression string.
     */
    public void setConstraint(String expr) {
        expression = expr;
        dirty = true;
        /*try {
            xpath = new XPath(expression, prefixResolver);
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException(te);
        }*/
        // constraintEngine.startTimer();
        // long t1 = System.currentTimeMillis();
        xpath = constraintEngine.getCachedXPath(expr, prefixResolver);
        // pretend = true;
        // long t2 = System.currentTimeMillis();
        // for (int i = 0; i < 100; i++) {
            calculateDependencies();
        // }
        // long t3 = System.currentTimeMillis();
        // pretend = false;
        // System.out.println("parsing: " + (t2 - t1) / 100.0);
        // System.out.println("analysis of " + expr + ": " + (t3 - t2) / 100.0);
        // constraintEngine.stopTimer();
        if (constraintAnimation != null) {
            constraintAnimation.getAnimationEngine().queuePendingAnimations(
                    new Animation[] { constraintAnimation });
        }
    }
    
    /**
     * Set the ConstraintAnimation object that owns this Constraint.
     */
    public void setConstraintAnimation(ConstraintAnimation ca) {
        constraintAnimation = ca;
        constraintAnimation.getAnimationEngine().queuePendingAnimations(
                new Animation[] { constraintAnimation });
    }
    
    /**
     * Get the ConstraintAnimation object that owns this Constraint.
     */
    public ConstraintAnimation getConstraintAnimation() {
        return constraintAnimation;
    }

    /**
     * Get the element this constraint is working on.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Get the attribute's QName.
     */
    public String getAttributeQName() {
        return attributeQName;
    }

    /**
     * Get the attribute's namespace URI.
     */
    public String getAttributeNamespaceURI() {
        return attributeNamespaceURI;
    }

    /**
     * Get the attribute's local name.
     */
    public String getAttributeLocalName() {
        return attributeLocalName;
    }

    /**
     * Get the type of the attribute this constraint is working on.
     */
    public short getAttributeType() {
        return attributeType;
    }

    /**
     * Dump the contents of a nodelist.
     */
    public static void dumpNodeList(NodeList nl) {
        int len = nl.getLength();
        System.out.println("NodeList (" + len + " items):");
        for (int i = 0; i < len; i++) {
            Node n = nl.item(i);
            System.out.print("  " + i + " (");
            switch (n.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    System.out.print("Attr");
                    break;
                case Node.CDATA_SECTION_NODE:
                    System.out.print("CDATASection");
                    break;
                case Node.COMMENT_NODE:
                    System.out.print("Comment");
                    break;
                case Node.ELEMENT_NODE:
                    System.out.print("Element");
                    break;
                case Node.TEXT_NODE:
                    System.out.print("Text");
                    break;
            }
            System.out.println(") " + n.getNodeName() + " " + n.getNodeValue());
        }
    }

    public static void dumpDocument(Node n, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.print("<" + n.getNodeName());
        NamedNodeMap nnm = n.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Attr a = (Attr) nnm.item(i);
            if (!a.getNodeName().startsWith("xmlns")) {
                System.out.print(" " + a.getNodeName() + "=\"" + a.getNodeValue() + "\"");
            }
        }
        System.out.println(">");
        for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
            if (m.getNodeType() == Node.ELEMENT_NODE) {
                dumpDocument(m, level + 1);
            }
        }
    }
    
    protected void propagateChanges(Set rds) {
        if (rds != null) {
            Iterator i = rds.iterator();
            while (i.hasNext()) {
                Constraint c = (Constraint) i.next();
                // System.out.println("=== propagate to " + c);
                c.markDirty();
                ConstraintAnimation ca = c.getConstraintAnimation();
                ca.getAnimationEngine().queuePendingAnimations(
                        new Animation[] { ca });
            }
        }
    }

    // static java.util.Stack st = new java.util.Stack();

    static int st = 0;
    static int count = 0;

    /**
     * Get the value of the constraint's expression.
     */
    public Value getValue() {
        if (!dirty && !expressionUsesTime) {
            return cachedValue;
        }
        try {
            // System.out.print("/// {" + number + "} calculate " + element.getTagName() + "#" + element.getAttributeNS(null, "id")
            //         + "." + attributeLocalName + " = " + expression + ": ");
            // System.out.println("Executing XPath: " + expression);
            // dumpDocument(element.getOwnerDocument(), 0);

            // System.out.println("[" + st + "] " + expression);
            // long t1 = java.util.Calendar.getInstance().getTime().getTime();
            // for (int i = 0; i < 100; i++) {
            //     xpath.execute(constraintEngine.xpathContext, element, prefixResolver);
            // }
            // long t2 = System.currentTimeMillis();
            st++;
            //constraintEngine.xpathContext = new XPathContext(constraintEngine);
            
            
            //xpath = new XPath(expression, prefixResolver);
            //System.out.println("Constraint.getValue() width context: " + constraintEngine.xpathContext);
            //constraintEngine.xpathContext.reset(); // xgx
            XObject xo = xpath.execute(constraintEngine.xpathContext, element, prefixResolver);
            st--;
            // long t3 = System.currentTimeMillis();
            // if (st == 0) {
            //     System.out.println("evaluation: " + (t2 - t1) / 100.0);
            // }
            Value v = Value.createValue(/*constraintEngine,*/ xo);
            Value newValue;
            if (type == Value.TYPE_UNKNOWN) {
                newValue = v;
            } else {
                newValue = v.convertTo(type, element);
            }
            if (!newValue.equals(cachedValue)) {
                // System.out.println(newValue);
                cachedValue = newValue;
                // propagate changes
                // System.out.println("==  propagating:");
                // System.out.println("==  getting constraints that depend on " + element.getNodeName() + "/@" + attributeNamespaceURI + ":" + attributeLocalName);
                propagateChanges(
                        constraintEngine.getReverseDependencies(element,
                                                                attributeNamespaceURI, 
                                                                attributeLocalName));
                propagateChanges(
                        constraintEngine.getReverseDependencies(element,
                                                                null, 
                                                                null));
                propagateChanges(
                        constraintEngine.getReverseBboxDependencies(element));
            } else {
                // System.out.println("unnecessary");
            }
            // System.out.println("Result: " + cachedValue);
            dirty = false;
            return cachedValue;
        } catch (javax.xml.transform.TransformerException te) {
            constraintEngine.stopTimer();
            throw new ConstraintException("Error evaluating XPath expression: "
                    + te.getMessageAndLocation(), te);
        }
    }

    /**
     * Get the dirty bit.
     */
    public boolean getDirty() {
        return dirty;
    }
    
    /**
     * Mark the constraint as dirty.
     */
    public void markDirty() {
        dirty = true;
    }

    /**
     * Get the cached value of the constraint.
     */
    public Value getCachedValue() {
        return cachedValue;
    }
    
    /**
     * Turn this constraint into a printable string.
     */
    public String toString() {
        String id = element.getAttributeNS(null, "id");
        return element.getTagName() + (id.equals("") ? "" : "#" + id) + "."
            + (attributeNamespaceURI == null || attributeNamespaceURI.equals("") ? "" : attributeNamespaceURI + ":")
            + attributeLocalName + " = " + expression;
    }

    protected void addDependency(Dependency d) {
        dependencies.addLast(d);
    }

    /**
     * Get the list of dependencies this constraint has.
     */
    public List getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
    
    protected static boolean matchNode(Node n, int nodetest, String ns, String ln, PrefixResolver pr) {
        int t = n.getNodeType();
        String nns, nln;
        if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            String an = ((Attr) n).getName();
            nln = DOMUtilities.getLocalName(an);
            String prefix = DOMUtilities.getPrefix(an);
            if (prefix == null) {
                nns = null;
            } else {
                nns = pr.getNamespaceForPrefix(prefix, ((Attr) n).getOwnerElement());
            }
        } else {
            nns = n.getNamespaceURI();
            nln = n.getLocalName();
        }
        // System.out.println("matchNode(" + n.getNodeName() + ", " + nodetest + ", " + ns + ", " + ln + ")");
        // System.out.println("  nns == " + nns);
        // System.out.println("  nln == " + nln);
        return nodetest == OpCodes.NODETYPE_NODE
                || nodetest == OpCodes.NODENAME
                    && (t == Node.ELEMENT_NODE || t == Node.ATTRIBUTE_NODE)
                    && (((ns == null || ns.equals("")) && (nns == null || nns.equals(""))) || (ns != null && ns.equals(nns)))
                    && (ln.equals("*") || ln.equals(nln))
                || nodetest == OpCodes.NODETYPE_COMMENT
                    && t == Node.COMMENT_NODE
                || nodetest == OpCodes.NODETYPE_TEXT 
                    && (t == Node.CDATA_SECTION_NODE || t == Node.TEXT_NODE)
                || nodetest == OpCodes.NODETYPE_PI
                    && t == Node.PROCESSING_INSTRUCTION_NODE
                    && (ln == null || ln.equals(n.getNodeName()));
    }
    
    protected String nodetestString(int nodetest, String ns, String ln) {
        if (nodetest == OpCodes.NODETYPE_NODE) {
            return "node()";
        } else if (nodetest == OpCodes.NODENAME) {
            return (ns == null ? ln : ns + ":" + ln);
        } else if (nodetest == OpCodes.NODETYPE_COMMENT) {
            return "comment()";
        } else if (nodetest == OpCodes.NODETYPE_TEXT) {
            return "text()";
        } else if (nodetest == OpCodes.NODETYPE_PI) {
            return "processing-instruction(" + (ln == null ? "" : "'" + ln + "'") + ")";
        } else {
            return "unknown";
        }
    }
    
    protected void recurseDependenciesDescendants(Compiler c, int nextStepPos, int end, Node n, boolean orSelf, boolean doneNextAxis, int nodetest, String ns, String ln) {
        if (orSelf) {
            if (matchNode(n, nodetest, ns, ln, prefixResolver)) {
                // System.out.println("node dependency: " + n);
                recurseDependenciesLocPath(c, nextStepPos, end, n, false, orSelf ? OpCodes.FROM_DESCENDANTS_OR_SELF : OpCodes.FROM_DESCENDANTS);
                doneNextAxis = true;
            }
        }
        Node m = n.getFirstChild();
        while (m != null) {
            if (matchNode(m, nodetest, ns, ln, prefixResolver)) {
                // System.out.println("node dependency: " + m);
                recurseDependenciesLocPath(c, nextStepPos, end, m, false, doneNextAxis ? -1 : OpCodes.FROM_DESCENDANTS);
                doneNextAxis = true;
            }
            recurseDependenciesDescendants(c, nextStepPos, end, m, false, doneNextAxis, nodetest, ns, ln);
            m = m.getNextSibling();
        }
    }

    protected void recurseDependenciesLocPath(Compiler c, int opPos, int end, Node n, boolean inPredicate, int previousAxis) {
        if (opPos >= end || c.getOp(opPos) == OpCodes.ENDOP) {
            return;
        }
        int op = c.getOp(opPos);
        int nextStepPos = c.getNextOpPos(opPos);
        int nextOpPos, op2, nspos, lnpos;
        Node m;
        String ln, ns;
        switch (op) {
            case OpCodes.OP_GROUP:
                recurseDependenciesLocPath(c, c.getFirstChildPos(opPos), end, n, inPredicate, previousAxis);
                break;
            case OpCodes.OP_VARIABLE:
                nspos = c.getOp(c.getFirstChildPos(opPos));
                lnpos = c.getOp(c.getFirstChildPos(opPos) + 1);
                ns = nspos == OpCodes.EMPTY ? null : (String) c.getToken(nspos);
                ln = (String) c.getToken(lnpos);
                if (previousAxis != -1) {
                    // System.out.println("* variable dependency on . (" + n.getNodeName() + ") $" + (ns == null ? ln : ns + ":" + ln));
                    if (!pretend) {
                        addDependency(new VariableDependency(this, ns, ln));
                    }
                }
                if (inPredicate) {
                    recurseDependenciesLocPath(c, nextStepPos, end, n, inPredicate, previousAxis);
                } else {
                    XObject var = org.apache.batik.constraint.xpath.Variable.resolveVariable(n, ns, ln);
                    if (var != null) {
                        try {
                            NodeList nl = var.nodelist();
                            int len = nl.getLength();
                            for (int i = 0; i < len; i++) {
                                m = nl.item(i);
                                // System.out.println("node dependency: " + m);
                                recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, previousAxis);
                            }
                        } catch (javax.xml.transform.TransformerException te) {
                            throw new ConstraintException("XPath error while analysing dependencies: "
                                    + te.getMessage(), te);
                        }
                    } else {
                        throw new ConstraintException("Cannot resolve variable $"
                                + (ns == null ? ln : ns + ":" + ln));
                    }
                }
                break;
            case OpCodes.OP_FUNCTION:
                int funcnum = c.getOp(c.getFirstChildPos(opPos));
                try {
                    Function f = FunctionTable.getFunction(funcnum);
                    if (f instanceof FuncId) {
                        constraintEngine.xpathContext.pushCurrentNode(constraintEngine.xpathContext.getDTMHandleFromNode(n));
                        Expression e = c.compile(opPos);
                        Expression arg0 = ((FuncId) e).getArg0();
                        String ref = arg0.execute(constraintEngine.xpathContext).toString();
                        // System.out.println("* id dependency \"" + ref + "\"");
                        if (!pretend) {
                            addDependency(new AttributeDependency(this,
                                                                  n.getOwnerDocument().getDocumentElement(),
                                                                  null,
                                                                  "id",
                                                                  true,
                                                                  true));
                        }
                        if (!inPredicate) {
                            /*XObject result = e.execute(constraintEngine.xpathContext);
                            NodeList nl = result.nodelist();
                            int len = nl.getLength();
                            System.out.println("Executing id returns " + len + " nodes");
                            for (int i = 0; i < len; i++) {
                                m = nl.item(i);
                                // System.out.println("calling recurseDependenciesLocPath with previousAxis=" + previousAxis);
                                recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, previousAxis);
                            }*/
                            m = n.getOwnerDocument().getElementById(ref);
                            if (m != null) {
                                recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, previousAxis);
                            } else {
                                System.out.println("  no element found");
                            }
                        }
                    }
                    opPos += 3;
                    for (;;) {
                        recurseDependencies(c, opPos, n, inPredicate);
                        if (c.getOp(opPos) == OpCodes.ENDOP) {
                            break;
                        }
                        opPos = c.getNextOpPos(opPos);
                    }
                } catch (javax.xml.transform.TransformerException te) {
                }
                break;
            case OpCodes.OP_EXTFUNCTION:
                nspos = c.getOp(c.getFirstChildPos(opPos));
                lnpos = c.getOp(c.getFirstChildPos(opPos) + 1);
                ns = nspos == OpCodes.EMPTY ? null : (String) c.getToken(nspos);
                ln = (String) c.getToken(lnpos);
                if (Constants.CSVG_NAMESPACE_URI.equals(ns)) {
                    if (ln.equals("instance")) {
                        m = FuncInstance.findInstanceElement(n);
                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, previousAxis);
                    }
                }
                end = c.getNextOpPos(opPos);
                opPos += 4;
                while (opPos < end) {
                    recurseDependencies(c, opPos, n, inPredicate);
                    if (c.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }
                    opPos = c.getNextOpPos(opPos);
                }
                break;
            default:
                if (op >= OpCodes.AXES_START_TYPES && op <= OpCodes.AXES_END_TYPES) {
                    nextOpPos = c.getNextOpPos(opPos);
                    opPos += 3;
                    ln = null;
                    ns = null;
                    op2 = c.getOp(opPos);
                    switch (op2) {
                        case OpCodes.NODETYPE_COMMENT:
                        case OpCodes.NODETYPE_TEXT:
                        case OpCodes.NODETYPE_NODE:
                        case OpCodes.NODETYPE_ROOT:
                        case OpCodes.NODETYPE_ANYELEMENT:
                        case OpCodes.NODETYPE_FUNCTEST:
                            opPos++;
                            break;
                        case OpCodes.NODETYPE_PI:
                            lnpos = c.getOp(opPos + 1);
                            ln = lnpos == OpCodes.EMPTY ? null : (String) c.getToken(lnpos);
                            opPos += 2;
                            break;
                        case OpCodes.NODENAME:
                            nspos = c.getOp(opPos + 1);
                            lnpos = c.getOp(opPos + 2);
                            ns = nspos == OpCodes.EMPTY ? null : (String) c.getToken(nspos);
                            ln = lnpos == OpCodes.ELEMWILDCARD ? "*" : (String) c.getToken(lnpos);
                            opPos += 3;
                            break;
                    }
                    if (previousAxis != -1) {
                        while (opPos < nextOpPos) {
                            // look at predicates of this FROM_*
                            recurseDependencies(c, opPos, n, inPredicate);
                            opPos = c.getNextOpPos(opPos);
                        }
                    }
                    if (inPredicate) {
                        // if in a predicate, just go to the next step without executing that step
                        recurseDependenciesLocPath(c, nextStepPos, end, n, inPredicate, op2);
                    } else {
                        // if not in a predicate, execute the step and for each node matched
                        // call the next step
                        switch (op) {
                            case OpCodes.FROM_ANCESTORS:
                            case OpCodes.FROM_ANCESTORS_OR_SELF:
                                m = op == OpCodes.FROM_ANCESTORS ? n.getParentNode() : n;
                                while (m != null) {
                                    if (matchNode(m, op2, ns, ln, prefixResolver)) {
                                        // System.out.println("node dependency: " + m);
                                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, op2);
                                    }
                                    m = m.getParentNode();
                                }
                                break;
                            case OpCodes.FROM_ATTRIBUTES:
                                NamedNodeMap nnm = n.getAttributes();
                                if (!pretend) {
                                    if (previousAxis == OpCodes.FROM_DESCENDANTS) {
                                        // System.out.println("* add/remove/modify on . (" + n.getNodeName() + ") (recursive, excl self) for attribute " + nodetestString(op2, ns, ln));
                                        addDependency(new AttributeDependency(this,
                                                                              n,
                                                                              ns,
                                                                              ln,
                                                                              true,
                                                                              false));
                                    } else if (previousAxis == OpCodes.FROM_DESCENDANTS_OR_SELF) {
                                        // System.out.println("* add/remove/modify on . (" + n.getNodeName() + ") (recursive) for attribute " + nodetestString(op2, ns, ln));
                                        addDependency(new AttributeDependency(this,
                                                                              n,
                                                                              ns,
                                                                              ln,
                                                                              true,
                                                                              true));
                                    } else if (previousAxis != -1) {
                                        // System.out.println("* add/remove/modify on . (" + n.getNodeName() + ") for attribute " + nodetestString(op2, ns, ln));
                                        addDependency(new AttributeDependency(this,
                                                                              n,
                                                                              ns,
                                                                              ln,
                                                                              false,
                                                                              false));
                                    }
                                }
                                /*if (ln != null && !ln.equals("*")) {
                                    m = nnm.getNamedItemNS(ns, ln);
                                    if (m != null) {
                                        System.out.println("node dependency: " + m);
                                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, false);
                                    }
                                } else {
                                    for (int i = 0; i < nnm.getLength(); i++) {
                                        m = nnm.item(i);
                                        if (matchNode(m, op2, ns, ln)) {
                                            System.out.println("node dependency: " + m);
                                            recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, false);
                                        }
                                    }
                                }*/
                                break;
                            case OpCodes.FROM_CHILDREN:
                                if (!pretend) {
                                    if (previousAxis == OpCodes.FROM_DESCENDANTS) {
                                        // System.out.println("* add/remove on . (" + n.getNodeName() + ") (recursive, excl self) for " + nodetestString(op2, ns, ln));
                                        addDependency(new NodeDependency(this,
                                                                         n,
                                                                         op2,
                                                                         ns,
                                                                         ln,
                                                                         true,
                                                                         false));
                                    } else if (previousAxis == OpCodes.FROM_DESCENDANTS_OR_SELF) {
                                        // System.out.println("* add/remove on . (" + n.getNodeName() + ") (recursive) for " + nodetestString(op2, ns, ln));
                                        addDependency(new NodeDependency(this,
                                                                         n,
                                                                         op2,
                                                                         ns,
                                                                         ln,
                                                                         true,
                                                                         true));
                                    } else if (previousAxis != -1) {
                                        // System.out.println("* add/remove on . (" + n.getNodeName() + ") for " + nodetestString(op2, ns, ln));
                                        addDependency(new NodeDependency(this,
                                                                         n,
                                                                         op2,
                                                                         ns,
                                                                         ln,
                                                                         false,
                                                                         false));
                                    }
                                }
                                m = n.getFirstChild();
                                while (m != null) {
                                    if (matchNode(m, op2, ns, ln, prefixResolver)) {
                                        // System.out.println("node dependency: " + m);
                                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, op2);
                                    }
                                    m = m.getNextSibling();
                                }
                                break;
                            case OpCodes.FROM_DESCENDANTS:
                                recurseDependenciesDescendants(c, nextStepPos, end, n, false, false, op2, ns, ln);
                                break;
                            case OpCodes.FROM_DESCENDANTS_OR_SELF:
                                recurseDependenciesDescendants(c, nextStepPos, end, n, true, false, op2, ns, ln);
                                break;
                            case OpCodes.FROM_FOLLOWING:
                                throw new ConstraintException("Cannot handle dependencies on following axis.");
                            case OpCodes.FROM_PRECEDING:
                                throw new ConstraintException("Cannot handle dependencies on preceding axis.");
                            case OpCodes.FROM_FOLLOWING_SIBLINGS:
                                // System.out.println("* add/remove on .. (" + n.getParentNode().getNodeName() + ") for " + nodetestString(op2, ns, ln));
                                if (!pretend) {
                                    addDependency(new NodeDependency(this,
                                                                     n.getParentNode(),
                                                                     op2,
                                                                     ns,
                                                                     ln,
                                                                     false,
                                                                     false));
                                }
                                m = n.getNextSibling();
                                while (m != null) {
                                    if (matchNode(m, op2, ns, ln, prefixResolver)) {
                                        // System.out.println("node dependency: " + m);
                                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, op2);
                                    }
                                    m = m.getNextSibling();
                                }
                                break;
                            case OpCodes.FROM_PARENT:
                                m = n.getParentNode();
                                if (matchNode(m, op2, ns, ln, prefixResolver)) {
                                    // System.out.println("node dependency: " + m);
                                    recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, op2);
                                }
                                break;
                            case OpCodes.FROM_PRECEDING_SIBLINGS:
                                // System.out.println("* add/remove on .. (" + n.getParentNode().getNodeName() + ") for " + nodetestString(op2, ns, ln));
                                addDependency(new NodeDependency(this,
                                                                 n.getParentNode(),
                                                                 op2,
                                                                 ns,
                                                                 ln,
                                                                 false,
                                                                 false));
                                m = n.getPreviousSibling();
                                while (m != null) {
                                    if (matchNode(m, op2, ns, ln, prefixResolver)) {
                                        // System.out.println("node dependency: " + m);
                                        recurseDependenciesLocPath(c, nextStepPos, end, m, inPredicate, op2);
                                    }
                                    m = m.getPreviousSibling();
                                }
                                break;
                            case OpCodes.FROM_SELF:
                                if (matchNode(n, op2, ns, ln, prefixResolver)) {
                                    // System.out.println("node dependency: " + n);
                                    recurseDependenciesLocPath(c, nextStepPos, end, n, inPredicate, op2);
                                }
                                break;
                            case OpCodes.FROM_NAMESPACE:
                                throw new ConstraintException("Cannot handle dependencies on namespace axis.");
                            case OpCodes.FROM_ROOT:
                                recurseDependenciesLocPath(c, nextStepPos, end, n.getOwnerDocument(), inPredicate, op2);
                                break;
                        }
                    }
                }
        }
    }

    protected void recurseDependencies(Compiler c, int opPos, Node n, boolean inPredicate) {
    	
        int op = c.getOp(opPos);
        int op2, nspos, lnpos, end;
        String ns, ln;
        switch (op) {
            case OpCodes.OP_XPATH:
            case OpCodes.OP_NEG:
            case OpCodes.OP_STRING:
            case OpCodes.OP_BOOL:
            case OpCodes.OP_NUMBER:
            case OpCodes.OP_GROUP:
            case OpCodes.OP_ARGUMENT:
                recurseDependencies(c,
                                    c.getFirstChildPos(opPos), 
                                    n, 
                                    inPredicate);
                break;
            case OpCodes.OP_OR:
            case OpCodes.OP_AND:
            case OpCodes.OP_NOTEQUALS:
            case OpCodes.OP_EQUALS:
            case OpCodes.OP_LTE:
            case OpCodes.OP_LT:
            case OpCodes.OP_GTE:
            case OpCodes.OP_GT:
            case OpCodes.OP_PLUS:
            case OpCodes.OP_MINUS:
            case OpCodes.OP_MULT:
            case OpCodes.OP_DIV:
            case OpCodes.OP_MOD:
            case OpCodes.OP_QUO:
                recurseDependencies(c,
                                    c.getFirstChildPos(opPos), 
                                    n,
                                    inPredicate);
                recurseDependencies(c, 
                                    c.getNextOpPos(c.getFirstChildPos(opPos)), 
                                    n,
                                    inPredicate);
                break;
            case OpCodes.OP_PREDICATE:
                recurseDependencies(c, c.getFirstChildPos(opPos), n, true);
                break;
            case OpCodes.OP_VARIABLE:
                nspos = c.getOp(c.getFirstChildPos(opPos));
                lnpos = c.getOp(c.getFirstChildPos(opPos) + 1);
                ns = nspos == OpCodes.EMPTY ? null : (String) c.getToken(nspos);
                ln = (String) c.getToken(lnpos);
                // System.out.println("* variable dependency on . (" + n.getNodeName() + ") $" + (ns == null ? ln : ns + ":" + ln));
                if (!pretend) {
                    addDependency(new VariableDependency(this, ns, ln));
                }
                break;
            case OpCodes.OP_EXTFUNCTION:
                nspos = c.getOp(c.getFirstChildPos(opPos));
                lnpos = c.getOp(c.getFirstChildPos(opPos) + 1);
                ns = nspos == OpCodes.EMPTY ? null : (String) c.getToken(nspos);
                ln = (String) c.getToken(lnpos);
                // System.out.println("function dependency: " + (ns != null ? ns + ":" : "") + ln);
                if (Constants.CSVG_NAMESPACE_URI.equals(ns)) {
                    if (ln.equals("bbox")) {
                        try {
                        	// xgx: reset xpath context (cursor, ...)
                        	//constraintEngine.xpathContext = new XPathContext();
                        	//constraintEngine.xpathContext.reset();
                            constraintEngine.xpathContext.pushCurrentNode(constraintEngine.xpathContext.getDTMHandleFromNode(n));
                            Expression e = c.compile(opPos);
                            Expression arg0 = ((FuncExtFunction) e).getArg(0);
                            
                            NodeList nl = arg0.execute(constraintEngine.xpathContext).nodelist();
                            // if (nl.getLength() > 0) {
                                Node m = nl.item(0);
                                // System.out.println("* bbox dependency on " + m.getNodeName());
                                if (!pretend) {
                                    addDependency(new BboxDependency(this, (Element) m));
                                }
                            // } else {
                            //     System.out.println("  bbox arg returned no nodes");
                            // }
                        } catch (javax.xml.transform.TransformerException te) {
                            throw new ConstraintException("Error analysing bbox dependency: "
                                    + te.getMessage(), te);
                        }
                    } else if (ln.equals("viewport")) {
                        // System.out.println("* viewport dependency");
                        if (!pretend) {
                            addDependency(new ViewportDependency(this, n));
                        }
                    } else if (ln.equals("time")) {
                        // System.out.println("* time dependency");
                        expressionUsesTime = true;
                    } else if (ln.equals("ctm")) {
                        Node m = n;
                        while (m != null && m.getNodeType() == Node.ELEMENT_NODE) {
                            if (!pretend) {
                                addDependency(new AttributeDependency(this,
                                                                      m,
                                                                      null,
                                                                      "transform",
                                                                      false,
                                                                      false));
                            }
                            m = m.getParentNode();
                        }
                    } else if (ln.equals("screenCTM")) {
                        Node m = n;
                        while (m != null && m.getNodeType() == Node.ELEMENT_NODE) {
                            if (!pretend) {
                                addDependency(new AttributeDependency(this,
                                                                      m,
                                                                      null,
                                                                      "transform",
                                                                      false,
                                                                      false));
                            }
                            m = m.getParentNode();
                        }
                        if (!pretend) {
                            addDependency(
                                    new ZoomAndPanDependency(this,
                                                             n.getOwnerDocument().getDocumentElement()));
                        }
                    } else if (ln.equals("LengthH") || ln.equals("LengthV")) {
                        // to handle percentage values when the viewport changes size
                        Node m = n;
                        while (m != null && m.getNodeType() == Node.ELEMENT_NODE) {
                            if (m.getNamespaceURI().equals(SVG_NAMESPACE_URI)
                                    && m.getLocalName().equals(SVG_SVG_TAG)) {
                                if (!pretend) {
                                    addDependency(new AttributeDependency(this, m, null, "width", false, false));
                                    addDependency(new AttributeDependency(this, m, null, "height", false, false));
                                }
                            }
                            m = m.getParentNode();
                        }
                    }
                }
                end = c.getNextOpPos(opPos);
                opPos += 4;
                while (opPos < end) {
                    recurseDependencies(c, opPos, n, inPredicate);
                    if (c.getOp(opPos) == OpCodes.ENDOP) {
                        break;
                    }
                    opPos = c.getNextOpPos(opPos);
                }
                break;
            case OpCodes.OP_FUNCTION:
                int funcnum = c.getOp(c.getFirstChildPos(opPos));
                try {
                    Function f = FunctionTable.getFunction(funcnum);
                    if (f instanceof FuncId) {
                        constraintEngine.xpathContext.pushCurrentNode(constraintEngine.xpathContext.getDTMHandleFromNode(n));
                        Expression e = c.compile(opPos);
                        Expression arg0 = ((FuncId) e).getArg0();
                        // System.out.println("* id dependency \"" + arg0.execute(constraintEngine.xpathContext).toString() + "\"");
                        if (!pretend) {
                            addDependency(new AttributeDependency(this, 
                                                                  n.getOwnerDocument(),
                                                                  null,
                                                                  "id",
                                                                  true,
                                                                  true));
                        }
                    }
                    opPos += 3;
                    for (;;) {
                        recurseDependencies(c, opPos, n, inPredicate);
                        if (c.getOp(opPos) == OpCodes.ENDOP) {
                            break;
                        }
                        opPos = c.getNextOpPos(opPos);
                    }
                } catch (javax.xml.transform.TransformerException te) {
                }
                break;
            case OpCodes.OP_LOCATIONPATH:
                end = c.getNextOpPos(opPos);
                opPos += 2;
                recurseDependenciesLocPath(c, opPos, end, n, inPredicate, OpCodes.FROM_SELF);
                break;
        }
    }

    public void calculateDependencies() {
        Compiler c = xpath.getCompiler();
        dependencies.clear();
        expressionUsesTime = false;
        recurseDependencies(c, 0, element, false);
    }

    /**
     * Returns whether the constraint refers to the c:time() function.
     */
    public boolean usesTime() {
        return expressionUsesTime;
    }

    // ExpressionOrigin //////////////////////////////////////////////////////

    /**
     * Get the context of the whole expression being evaluated, for use as
     * a context node for variable resolution.
     */
    public Node getExpressionOrigin() {
        return element;
    }
}
