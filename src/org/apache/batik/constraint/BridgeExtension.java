package org.apache.batik.constraint;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Element;

/**
 * Service class to register bridges for the constraint elements.
 */
public class BridgeExtension
    implements org.apache.batik.bridge.BridgeExtension,
               Constants {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority() { return 2f; }

    /**
     * This should return the list of extensions implemented
     * by this BridgeExtension.
     * @return An iterator containing strings one for each implemented
     *         extension.
     */
    public Iterator getImplementedExtensions() {
        String [] extensions = {
            CSVG_NAMESPACE_URI
        };
        Vector v = new Vector(extensions.length);
        for (int i=0; i<extensions.length; i++) {
            v.add(extensions[i]);
        }
        return Collections.unmodifiableList(v).iterator();
    }

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor() {
        return "Cameron McCormack";
    }

    /**
     * This should contain a contact address (usually an e-mail address).
     */
    public String getContactAddress() {
        return "cam@" + "mcc.id.au";
    }

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL() {
        return "http://www.csse.monash.edu.au/~clm/csvg/";
    }

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription() {
        return "Extension to support constraints in SVG";
    }

    /**
     * This method should update the BridgeContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The BridgeContext instance to be updated
     */
    public void registerTags(BridgeContext ctx) {
        ctx.putBridge(new VariableElementBridge());
        ctx.putBridge(new ConstraintElementBridge());
        ctx.putBridge(new PropertyElementBridge());
        ctx.putBridge(new TextElementBridge());
    }

    /**
     * Whether the presence of the specified element should cause
     * the document to be dynamic.  If this element isn't handled
     * by this BridgeExtension, just return false.
     *
     * @param e The element to check.
     */
    public boolean isDynamicElement(Element e) {
        String ns = e.getNamespaceURI();
        if (!CSVG_NAMESPACE_URI.equals(ns)) {
            return false;
        }
        // all four CSVG elements are dynamic, so just return true
        return true;
    }
}
