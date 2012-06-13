package org.apache.batik.constraint;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.DomExtension;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import org.apache.batik.dom.svg.ExtensibleSVGDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Service class to register DOM classes for the constraint elements.
 */
public class DOMExtension implements DomExtension, Constants {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority() { return 2f; }

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
        return "cam" + "@mcc.id.au";
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
     * This method should update the DomContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The DomContext instance to be updated
     */
    public void registerTags(ExtensibleSVGDOMImplementation di) {
        di.registerCustomElementFactory
            (CSVG_NAMESPACE_URI,
             CSVG_CONSTRAINT_TAG,
             new ConstraintElementFactory());

        di.registerCustomElementFactory
            (CSVG_NAMESPACE_URI,
             CSVG_VARIABLE_TAG,
             new VariableElementFactory());

        di.registerCustomElementFactory
            (CSVG_NAMESPACE_URI,
             CSVG_PROPERTY_TAG,
             new PropertyElementFactory());

        di.registerCustomElementFactory
            (CSVG_NAMESPACE_URI,
             CSVG_TVAL_TAG,
             new TValElementFactory());
    }

    /**
     * To create a 'constraint' element.
     */
    protected static class ConstraintElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public ConstraintElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new ConstraintElement
                (prefix, (AbstractDocument) doc);
        }
    }

    /**
     * To create a 'variable' element.
     */
    protected static class VariableElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public VariableElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new VariableElement(prefix, (AbstractDocument) doc);
        }
    }

    /**
     * To create a 'property' element.
     */
    protected static class PropertyElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public PropertyElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new PropertyElement(prefix, (AbstractDocument) doc);
        }
    }

    /**
     * To create a 'tval' element.
     */
    protected static class TValElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public TValElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new TValElement(prefix, (AbstractDocument) doc);
        }
    }

	@Override
	public void registerTags(ExtensibleDOMImplementation di) {
		// TODO Auto-generated method stub
	//	registerTags((ExtensibleSVGDOMImplementation)di);
		 di.registerCustomElementFactory
         (CSVG_NAMESPACE_URI,
          CSVG_CONSTRAINT_TAG,
          new ConstraintElementFactory());

	     di.registerCustomElementFactory
	         (CSVG_NAMESPACE_URI,
	          CSVG_VARIABLE_TAG,
	          new VariableElementFactory());
	
	     di.registerCustomElementFactory
	         (CSVG_NAMESPACE_URI,
	          CSVG_PROPERTY_TAG,
	          new PropertyElementFactory());
	
	     di.registerCustomElementFactory
	         (CSVG_NAMESPACE_URI,
	          CSVG_TVAL_TAG,
	          new TValElementFactory());
		}
    
}
