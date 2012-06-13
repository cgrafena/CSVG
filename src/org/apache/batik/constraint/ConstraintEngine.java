package org.apache.batik.constraint;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.AnimationEvent;
import org.apache.batik.anim.AnimationEventListener;
import org.apache.batik.anim.ConstraintAnimation;
import org.apache.batik.anim.ConstraintsAnimationEngine;
import org.apache.batik.constraint.values.Value;
import org.apache.batik.constraint.xpath.XPath;
import org.apache.batik.constraint.xpath.functions.ConstraintFunction;
import org.apache.batik.constraint.xpath.functions.FuncBbox;
import org.apache.batik.constraint.xpath.functions.FuncCTM;
import org.apache.batik.constraint.xpath.functions.FuncHeight;
import org.apache.batik.constraint.xpath.functions.FuncIf;
import org.apache.batik.constraint.xpath.functions.FuncInstance;
import org.apache.batik.constraint.xpath.functions.FuncInverse;
import org.apache.batik.constraint.xpath.functions.FuncLength;
import org.apache.batik.constraint.xpath.functions.FuncLengthH;
import org.apache.batik.constraint.xpath.functions.FuncLengthV;
import org.apache.batik.constraint.xpath.functions.FuncMatrix;
import org.apache.batik.constraint.xpath.functions.FuncMax;
import org.apache.batik.constraint.xpath.functions.FuncMin;
import org.apache.batik.constraint.xpath.functions.FuncPoint;
import org.apache.batik.constraint.xpath.functions.FuncProperty;
import org.apache.batik.constraint.xpath.functions.FuncScreenCTM;
import org.apache.batik.constraint.xpath.functions.FuncTest;
import org.apache.batik.constraint.xpath.functions.FuncTime;
import org.apache.batik.constraint.xpath.functions.FuncViewport;
import org.apache.batik.constraint.xpath.functions.FuncWidth;
import org.apache.batik.constraint.xpath.functions.FuncX;
import org.apache.batik.constraint.xpath.functions.FuncY;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.DoublyIndexedTable;
import org.apache.xpath.ExtensionsProvider;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncExtFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class that holds all constraints for a document and tracks dependencies.
 */
public class ConstraintEngine implements ExtensionsProvider {

    /**
     * The document this constraint engine is handling.
     */
    protected Document document;

    /**
     * The CSS engine used in the document.
     */
    protected CSSEngine cssEngine;

    /**
     * The animation engine this constraint engine will interact with.
     */
    protected ConstraintsAnimationEngine animationEngine;

    /**
     * List of all constraints in the system.
     */
    protected List constraints = new LinkedList();

    /**
     * Map of (Element, String) pairs to Constraints.
     */
    protected DoublyIndexedTable constraintsMap = new DoublyIndexedTable();

    /**
     * Map of (Element, String) pairs to Sets of Constraints.
     * This maps a constraint's target attribute to a set of constraints
     * which depend upon that attribute.
     */
    protected /*DoublyIndexedTable*/DependenciesMap reverseDependenciesMap = new DependenciesMap();//DoublyIndexedTable();

    /**
     * Map for Element to Sets of Constraints for reverse BBox dependencies.
     */
    protected Map reverseBboxDependenciesMap = new HashMap();
    
    /**
     * Map of CSVG function names to {org.apache.batik.xpath.ConstraintFunction}s.
     */
    protected HashMap functions = new HashMap();

    /**
     * Event handler for animation events.
     */
    protected AnimationEventHandler animationEventHandler;

    /**
     * The XPathContext used for evaluation.
     */
    XPathContext xpathContext;

    /**
     * Create a new ConstraintEngine.
     */
    public ConstraintEngine(Document doc) {
        document = doc;
        if (doc instanceof SVGOMDocument) {
            SVGOMDocument svgDoc = (SVGOMDocument) doc;
            animationEngine = svgDoc.getAnimationEngine();
            animationEventHandler = new AnimationEventHandler();
            animationEngine.addEventListener(animationEventHandler);
            cssEngine = svgDoc.getCSSEngine();
        }
        initialiseFunctions();      
        
        xpathContext = new XPathContext(this);        
        //System.out.println("created new xpathcontext " + xpathContext);
        //xpathContext.reset();
        
    }

    /**
     * Register CSVG extension XPath functions.
     */
    protected void initialiseFunctions() {
        initialiseFunction("Length", new FuncLength());
        initialiseFunction("LengthH", new FuncLengthH());
        initialiseFunction("LengthV", new FuncLengthV());
        initialiseFunction("Matrix", new FuncMatrix());
        initialiseFunction("Point", new FuncPoint());
        initialiseFunction("bbox", new FuncBbox());
        initialiseFunction("ctm", new FuncCTM());
        initialiseFunction("height", new FuncHeight());
        initialiseFunction("if", new FuncIf());
        initialiseFunction("instance", new FuncInstance());
        initialiseFunction("inverse", new FuncInverse());
        initialiseFunction("max", new FuncMax());
        initialiseFunction("min", new FuncMin());
        initialiseFunction("property", new FuncProperty());
        initialiseFunction("screenCTM", new FuncScreenCTM());
        initialiseFunction("test", new FuncTest());
        initialiseFunction("time", new FuncTime());
        initialiseFunction("viewport", new FuncViewport());
        initialiseFunction("width", new FuncWidth());
        initialiseFunction("x", new FuncX());
        initialiseFunction("y", new FuncY());
    }
    
    private void initialiseFunction(String fn, ConstraintFunction f) {
        f.setConstraintEngine(this);
        functions.put(fn, f);
    }
    
    /**
     * Get a function.
     */
    public ConstraintFunction getFunction(String name) {
        return (ConstraintFunction) functions.get(name);
    }
    
    /**
     * Dispose this constraint engine.
     */
    public void dispose() {
        document = null;
        cssEngine = null;
        animationEngine.removeEventListener(animationEventHandler);
        //animationEngine.dispose();
        animationEngine = null;
    }

    public ConstraintsAnimationEngine getAnimationEngine() {
        return animationEngine;
    }

    /**
     * Add a constraint to the system.
     */
    public void addConstraint(Constraint c) {
        // System.out.println("### adding constraint " + c);
        Element e = c.getElement();
        String n = c.getAttributeNamespaceURI() + ":" + c.getAttributeLocalName();
        // System.out.println("    e: " + e);
        // System.out.println("    n: " + n);
        Constraint existingConstraint = (Constraint) constraintsMap.get(e, n);
        if (existingConstraint == c) {
            // System.out.println("   same as old constraint, ignoring");
            return;
        }
        if (existingConstraint != null) {
            // replacing constraint
            if (existingConstraint.getExpression().equals(c.getExpression())) {
                // System.out.println("   same as old constraint, ignoring");
                return;
            }
            // System.out.println("   --- replacing existing constraint!");
            // XXX remove existing constraint and dependencies!
        }

        ConstraintAnimation ca = new ConstraintAnimation(animationEngine, c);
        c.setConstraintAnimation(ca);
        constraints.add(c);
        constraintsMap.put(e, n, c);
        Iterator i = c.getDependencies().iterator();
        while (i.hasNext()) {
            Dependency d = (Dependency) i.next();
            if (d instanceof AttributeDependency) {
                AttributeDependency ad = (AttributeDependency) d;
                Node pn = ad.getParentNode();
                String an = ad.getNamespaceURI() + ":" + ad.getLocalName();
                // System.out.println("-- rev: pn is " + pn + " " + pn.getNodeName() + "#" + ((Element) pn).getAttributeNS(null, "id") + ", an = " + an);
                Set rds = (Set) reverseDependenciesMap.get(pn, an);
                if (rds == null) {
                    // System.out.println("   new entry!");
                    rds = new HashSet();
                    reverseDependenciesMap.put(pn, an, rds);
                } else {
                    // System.out.println("   existing entry");
                }
                // System.out.println("### adding reverse dependency: [" + c + "] depends on " + pn.getNodeName() + "/@" + an);
                rds.add(c);
            } else if (d instanceof VariableDependency) {
                VariableDependency vd = (VariableDependency) d;
                Element ve = vd.getVariableElement();
                Set rds = (Set) reverseDependenciesMap.get(ve, ":value");
                if (rds == null) {
                    rds = new HashSet();
                    reverseDependenciesMap.put(ve, ":value", rds);
                }
                // System.out.println("### adding reverse dependency: [" + c + "] depends on variable " + ve.getAttributeNS(null, "name"));
                rds.add(c);
            } else if (d instanceof BboxDependency) {
                BboxDependency bd = (BboxDependency) d;
                Element be = bd.getElement();
                Set rds = (Set) reverseBboxDependenciesMap.get(be);
                if (rds == null) {
                    rds = new HashSet();
                    reverseBboxDependenciesMap.put(be, rds);
                }
                // System.out.println("### adding reverse dependency: [" + c + "] depends on bbox of " + be.getNodeName());
                rds.add(c);
            }
        }
        animationEngine.addAnimation(ca);
    }

    public void dumpReverseDependencies() {
        System.out.println("Reverse dependencies:");
        reverseDependenciesMap.dump();
        System.out.println("Reverse bbox dependencies:");
        Iterator i = reverseBboxDependenciesMap.keySet().iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (!(o instanceof Element)) {
                System.out.println("item is of type " + o.getClass().getName());
            }
            Element e = (Element) o;
            System.out.print("\tbbox(");
            String id = e.getAttributeNS(null, "id");
            if (e instanceof VariableElement) {
                System.out.print("$" + e.getAttributeNS(null, "name") + ") <-- { ");
            } else {
                System.out.print(e.getNodeName() + (!id.equals("") ? "#" + id : "") + ") <-- { ");
            }
            Set rds = (Set) reverseBboxDependenciesMap.get(e);
            Iterator j = rds.iterator();
            while (j.hasNext()) {
                Constraint c = (Constraint) j.next();
                System.out.print(c.toString() + ", ");
            }
            System.out.println("}");
        }
    }
    
    /**
     * Get the set of reverse bbox dependencies for the given element.
     */
    public Set getReverseBboxDependencies(Element e) {
        Set rds = (Set) reverseBboxDependenciesMap.get(e);
        if (rds == null) {
            return null;
        }
        return Collections.unmodifiableSet(rds);
    }
    
    /**
     * Get the set of reverse dependencies for the given constraint attribute.
     */
    public Set getReverseDependencies(Element e, String attrNS, String attrLocalName) {
        String an;
        if (attrNS == null) {
            if (attrLocalName == null) {
                an = "*";
            } else {
                an = ":" + attrLocalName;
            }
        } else {
            an = attrNS + ":" + attrLocalName;
        }
        Set rds = (Set) reverseDependenciesMap.get(e, an);
        if (rds == null) {
            return null;
        }
        return Collections.unmodifiableSet(rds);
    }
    
    /**
     * Get the constraint for the specified attribute.
     */
    public Constraint getConstraint(Element e,
                                    String attrNS,
                                    String attrLocalName) {
        if (attrNS == null) {
            attrNS = "";
        }
        return (Constraint) constraintsMap.get(e, attrNS + ":" + attrLocalName);
    }

    /**
     * Get the CSS engine.
     */
    public CSSEngine getCSSEngine() {
        return cssEngine;
    }

    /**
     * Dump the current constraints to the terminal.
     */
    public void dumpConstraints(boolean showValues) {
        StringBuffer sb = new StringBuffer();
        sb.append("Constraints:\n");
        Iterator i = constraints.iterator();
        while (i.hasNext()) {
            Constraint c = (Constraint) i.next();
            sb.append("\t" + c);
            try {
                if (showValues) {
                    sb.append(" ");
                    Value v = c.getValue();
                    sb.append("== (");
                    sb.append(v.getTypeName());
                    sb.append(") ");
                    sb.append(v.toString());
                } else {
                    if (c.getDirty()) {
                        sb.append(" (dirty) ");
                    } else {
                        sb.append(" ");
                        Value v = c.getCachedValue();
                        sb.append("== (");
                        sb.append(v.getTypeName());
                        sb.append(") ");
                        sb.append(v.toString());
                        sb.append(" (cached)");
                    }
                }
            } catch (ConstraintException ce) {
                ce.printStackTrace();
                sb.append("[" + ce.getMessage() + "]");
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
        if (showValues) {
            dumpReverseDependencies();
        }
    }

    /**
     * Force update of all constraints.
     */
    public void updateAllConstraints() {
        Object[] cs = constraints.toArray();
        ConstraintAnimation[] cas = new ConstraintAnimation[cs.length];
        for (int i = 0; i < cs.length; i++) {
            Constraint c = (Constraint) cs[i];
            c.markDirty();
            cas[i] = c.getConstraintAnimation();
        }
        animationEngine.queuePendingAnimations(cas);
    }

    /**
     * Cache of XPath objects.
     * XXX PrefixResolver shouldn't be the same for reused XPath objects!
     */
    protected HashMap cachedXPaths = new HashMap();

    XPath getCachedXPath(String expr, PrefixResolver pr)
            throws XPathException {
        XPath xpath = (XPath) cachedXPaths.get(expr);
        if (xpath == null) {
            try {
                for (int i = 0; i < 100; i++) {
                    xpath = new XPath(expr, pr);
                }
            }
            catch (javax.xml.transform.TransformerException te) {
                throw new XPathException(te);
            }
            cachedXPaths.put(expr, xpath);
        }
        return xpath;
    }

    /**
     * Class to handle animation events.
     */
    protected class AnimationEventHandler implements AnimationEventListener {

        protected void processSet(Set rds) {
            if (rds != null) {
                Iterator i = rds.iterator();
                while (i.hasNext()) {
                    Constraint c = (Constraint) i.next();
                    //System.out.println("[" + System.currentTimeMillis() + "]" + " dirty constraint " + c + " is queued again with new value: " + c.getValue());
                    c.markDirty();
                    
                    ConstraintAnimation ca = c.getConstraintAnimation();
                    
                    ca.getAnimationEngine().queuePendingAnimations(
                            new Animation[] { ca });
                }
            }
        }

        /**
         * Called when an animated CSS property or XML attribute is updated.
         */
        public void animatedValueUpdated(AnimationEvent evt) {
            Element e = evt.getElement();
            //System.out.println("[" + System.currentTimeMillis() + "]" + " animated value updated for element " + e + " attribute " + evt.getAttributeName());
            String an = evt.getAttributeName();
            String prefix = DOMUtilities.getPrefix(an);
            String ln = DOMUtilities.getLocalName(an);
            String ns;
            if (prefix == null) {
                ns = "";
            } else {
                PrefixResolver pr = new PrefixResolver(e);
                ns = pr.getNamespaceForPrefix(prefix, e);
            }
            processSet(getReverseDependencies(e, ns, ln));
            processSet(getReverseDependencies(e, null, null));
            while (e != null) {
                processSet(getReverseBboxDependencies(e));
               
                Node n = e.getParentNode();
                if (n == null || n.getNodeType() == Node.DOCUMENT_NODE) { // n == null by xgx
                    break;
                }
                if (n instanceof SVGOMCSSImportedElementRoot) {
                    n = ((SVGOMCSSImportedElementRoot) n).getCSSParentElement();
                }
                e = (Element) n;
            }
        }
    }

    long timerStart;
    long totalTime = 0;
    int timerStack = 0;

    void startTimer() {
        if (timerStack++ == 0) {
            // System.gc();
            timerStart = java.util.Calendar.getInstance().getTime().getTime();
        }
        // System.out.println("[" + timerStack + "] start");
    }

    void stopTimer() {
        timerStack--;
        // System.out.println("[" + timerStack + "] stop");
        if (timerStack == 0) {
            totalTime += java.util.Calendar.getInstance().getTime().getTime() - timerStart;
            System.out.println("========> time so far: " + ((double) totalTime / 1000.0));
        }
    }

    // ExtensionsProvider ////////////////////////////////////////////////////

    /**
     * Is the extension function available?
     */
    public boolean functionAvailable(String ns, String funcName)
            throws javax.xml.transform.TransformerException {
        if (ns.equals(Constants.CSVG_NAMESPACE_URI)) {
            return getFunction(funcName) != null;
        }
        return false;
    }

    /**
     * Is the extension element available?
     */
    public boolean elementAvailable(String ns, String funcName)
            throws javax.xml.transform.TransformerException {
        return false;
    }

    /**
     * Execute the extension function.
     */
    public Object extFunction(String ns, String funcName, 
            Vector argVec, Object methodKey)
            throws javax.xml.transform.TransformerException {
        ConstraintFunction f = getFunction(funcName);
        if (f == null) {
            throw new javax.xml.transform.TransformerException
                ("No such extension function " + ns + ":" + funcName);
        }
        return f.execute(xpathContext, argVec);
    }

    /**
     * Execute the extension function.
     */
    public Object extFunction(FuncExtFunction extFunction, 
            Vector argVec)
            throws javax.xml.transform.TransformerException {
        String fn = extFunction.getFunctionName();
        ConstraintFunction f = getFunction(fn);
        if (f == null) {
            throw new javax.xml.transform.TransformerException
                ("No such extension function " + fn);
        }
        return f.execute(xpathContext, argVec);
    }

}
