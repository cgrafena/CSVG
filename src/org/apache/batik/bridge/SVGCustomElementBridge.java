package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.anim.AnimationEvent;
import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMCustomElement;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

import org.apache.xalan.processor.TransformerFactoryImpl;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;

import java.util.HashMap;

/**
 * Bridge class for custom elements.
 */
public class SVGCustomElementBridge extends AbstractGraphicsNodeBridge {

    protected String namespaceURI;
    protected String localName;
    protected Element stylesheet;

    // protected Transformer transformer;
    protected static HashMap transformers = new HashMap();

    /**
     * Constructs a new bridge for the custom element.
     */
    public SVGCustomElementBridge(String ns, String ln, Element xslt) {
        namespaceURI = ns;
        localName = ln;
        stylesheet = xslt;
    }

    /**
     * Returns the custom element's local name.
     */
    public String getLocalName() {
        return localName;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGCustomElementBridge(namespaceURI, localName, stylesheet);
    }

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        CompositeGraphicsNode gn = buildCompositeGraphicsNode(ctx, e, null);

        return gn;
    }

    /**
     * Creates a <tt>GraphicsNode</tt> from the input element and
     * populates the input <tt>CompositeGraphicsNode</tt>
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param gn the CompositeGraphicsNode where the use graphical 
     *        content will be appended. The composite node is emptied
     *        before appending new content.
     */
    public CompositeGraphicsNode
            buildCompositeGraphicsNode(BridgeContext ctx, 
                                       Element e,
                                       CompositeGraphicsNode gn) {

        SVGOMCustomElement ce = (SVGOMCustomElement) e;
        Node oldRoot = ce.getCSSImportedElementRoot();

        // XXX shadow tree gets generated only once.  is this right?
        Element svgElement = null;
        if (oldRoot == null) {
            // create a new svg element to hold the transformed result
            SVGOMDocument document = (SVGOMDocument) e.getOwnerDocument();
            // long t1 = System.currentTimeMillis();
            // for (int i = 0; i < 100; i++) {
                svgElement = document.createElementNS(SVG_NAMESPACE_URI, SVG_SVG_TAG);
                // XXX hack, should inherit this overflow property from whereever the shadow
                // tree is inserted into the document.
                svgElement.setAttributeNS(null, "overflow", "visible");

                String nm = e.getAttributeNS(null, "id");
                if (nm.equals("")) {
                    nm = e.getAttributeNS(null, "name");
                }
                if (nm.equals("")) {
                    nm = e.toString();
                }
                // System.out.println("*** generating shadow tree for element " + e.getTagName() + " (" + nm + ")");

                // transform element e with the stylesheet, placing the result
                // under svgElement
                try {
                    Transformer transformer = (Transformer) transformers.get(stylesheet);
                    if (transformer == null) {
                        System.out.println("--- compiling stylsheet");
                        TransformerFactoryImpl tf = new TransformerFactoryImpl();
                        transformer = tf.newTransformer(new DOMSource(stylesheet));
                        transformers.put(stylesheet, transformer);
                    }
                    transformer.transform(new DOMSource(e), new DOMResult(svgElement));
                } catch (javax.xml.transform.TransformerException te) {
                    throw new RuntimeException(te);
                }
            // }
            // long t2 = System.currentTimeMillis();
            // System.out.println("shadow tree: " + (t2 - t1) / 100.0);

            // System.out.println("Shadow tree:");
            // org.apache.batik.constraint.Constraint.dumpDocument(svgElement, 1);

            // attach the generated content to the document
            UpdateManager um = ctx.getUpdateManager();
            ScriptingEnvironment se = um == null ? null
                                                 : um.getScriptingEnvironment();
            SVGOMCSSImportedElementRoot root;
            root = new SVGOMCSSImportedElementRoot(document, e, false);
            root.appendChild(svgElement);

            /*if (oldRoot != null) {
                if (se != null) {
                    se.removeScriptingListeners(oldRoot);
                }
                disposeTree(oldRoot);
            }*/
            ce.setCSSImportedElementRoot(root);
            if (se != null) {
                se.addScriptingListeners(root);
            }
        } else {
            svgElement = (Element) oldRoot.getFirstChild();
        }

        // no need to import cascaded style maps here?

        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode refNode = builder.build(ctx, svgElement);

        boolean update = true;
        if (gn == null) {
            gn = new CompositeGraphicsNode();
            update = false;
        }

        if (update) {
            int s = gn.size();
            for (int i = 0; i < s; i++) {
                gn.remove(0);
            }
        }

        gn.getChildren().add(refNode);

        // 'visibility'
        gn.setVisible(CSSUtilities.convertVisibility(e));

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }

        return gn;
    }

    public void dispose() {
        SVGOMCustomElement ce = (SVGOMCustomElement) e;
        if ((ce != null) &&
            (ce.getCSSImportedElementRoot() != null)) {
            disposeTree(ce.getCSSImportedElementRoot());
        }

        super.dispose();
    }

    /**
     * Creates the GraphicsNode depending on the GraphicsNodeBridge
     * implementation.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return null; // nothing to do, createGraphicsNode is fully overriden
    }

    /**
     * Returns false as the custom element is a not container.
     */
    public boolean isComposite() {
        // XXX maybe this should return true?
        return false;
    }

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {

        super.buildGraphicsNode(ctx, e, node);
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        // Currently do nothing, as rerunning the transformer automatically
        // is not supported.
    }

	@Override
	public void handleAnimationEvent(AnimationEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
