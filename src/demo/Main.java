package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGTextElement;


 
/**
 * LogoDemo
 *
 * @author cgrafena
 */
public class Main extends JApplet {

	private static final long serialVersionUID = -9072664017480216697L;

	protected MyJSVGCanvas canvas;
    protected SVGDocument doc;
    
    protected String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    private static final int BASE_FONT_SIZE = 80;

    // svg and css dom boot
    GraphicsNode rootGN;
    UserAgent userAgent;
    DocumentLoader loader;
    BridgeContext ctx;
    GVTBuilder builder;
    
    JButton jbtnUpdate, jbtnSave; 
    JTextField jtfHeadline, jtfDownline;
    JLabel jlHeadline, jlCompanyWebaddress, jlDownline, jlCompanyForm, jlFontFamily, jlStatus;
    JComboBox<String> jcbFontFamily;

    public void init() {
    	// set applet size
    	setSize(500, 500);
    	
		// init swing
		 try { 
		      SwingUtilities.invokeAndWait(new Runnable () { 
		        public void run() { 
		          guiInit(); // initialize the GUI 
		    } 
		  }); 
		} catch(Exception exc) { 
			System.out.println("Can't create swing because of "+ exc); 
			exc.printStackTrace();
		}
    
    initSVGDocument();
    }
    
    public void initSVGDocument() {
    	  // Create an SVG document.
    	
     	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();            
     	doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
     	
    	// init svg and css dom
    	userAgent = new UserAgentAdapter();
        loader    = new DocumentLoader(userAgent);
        
        ctx       = new BridgeContext(userAgent, loader, canvas);
        ctx.setDynamicState(BridgeContext.DYNAMIC);
        builder   = new GVTBuilder();
        rootGN    = builder.build(ctx, doc);
     	    	
    	// display document in the svg canvas
    	canvas.setSVGDocument(doc);
    }
    
    private void removeAllElementsFromCanvas() {    	
    	ArrayList<Node> al = new ArrayList<Node>();
    	 
    	// collect all nodes
    	Node n = doc.getDocumentElement().getFirstChild();
    	while (n != null) {
    		al.add(n);
    		n = n.getNextSibling();    			
    	}
    	// remove all nodes
    	for (Node x : al) {
    		doc.getDocumentElement().removeChild(x);
    	}    	    	
    	canvas.setSVGDocument(doc);    	
    }
    
    /**
     * creates a rectangle
     * @param id
     * @param offsetX
     * @param offsetY
     * @param width
     * @param height
     * @param fill
     * @return
     */
    private Element createRectangle(String id, String offsetX, String offsetY, String width, String height, String fill) {
    	Element e = doc.createElementNS(svgNS, SVGConstants.SVG_RECT_TAG);
        e.setAttribute("id", id);
        e.setAttribute( "x",offsetX);
        e.setAttribute("y",offsetY);
        e.setAttribute("width",width);
        e.setAttribute("height",height);
        e.setAttribute("fill", fill);
        return e;
    }
    
     
    public void paintStuff(String sHeadline, String sDownline) {
    	initSVGDocument();
    	SVGOMDocument svgDoc = (SVGOMDocument)doc;
  	 		
		doc.getRootElement().setAttributeNS(null, "xmlns:c", "http://mcc.id.au/2004/csvg");
		    		
   		Element headline = createTextNode(sHeadline, "headline", null, String.valueOf(BASE_FONT_SIZE), "black", "middle");
   		Element cHeadlineX = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cHeadlineX.setAttributeNS(null, "attributeName", "x");
   		cHeadlineX.setAttributeNS(null, "value", "c:width(c:viewport()) div 2");
   		
   		Element cHeadlineY = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cHeadlineY.setAttributeNS(null, "attributeName", "y");
   		cHeadlineY.setAttributeNS(null, "value", "c:height(c:viewport()) div 2");
   		
   		headline.appendChild(cHeadlineX);
   		headline.appendChild(cHeadlineY);
   		
   		Element downline = createTextNode(sDownline, "downline", null, "10", "black", null);
   		Element cDownlineX = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cDownlineX.setAttributeNS(null, "attributeName", "x");
   		cDownlineX.setAttributeNS(null, "value", "c:x(c:bbox(id('headline'))) + c:width(c:bbox(id('headline')))");
   		
   		Element cDownlineY = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cDownlineY.setAttributeNS(null, "attributeName", "y");
   		
   		cDownlineY.setAttributeNS(null, "value", "c:y(c:bbox(id('headline'))) + c:height(c:bbox(id('headline')))");
   		
   		downline.appendChild(cDownlineX);
   		downline.appendChild(cDownlineY);
   		
   		doc.getRootElement().appendChild(headline);
   		doc.getRootElement().appendChild(downline);
    		  
    }
    
    
    /**
     * creates a text node
     * @param nodeValue
     * @param nodeId
     * @param fontFamily
     * @param fontSize
     * @param textAlignment
     */
    private SVGTextElement createTextNode(String nodeValue, String nodeId, String fontFamily, String fontSize, String fillColor, String textAlignment) {
    	SVGTextElement tElem = (SVGTextElement) doc.createElementNS (svgNS, "text");

    	// Create the text node with the text that will become the content of the <text> element
        Text text = doc.createTextNode (nodeValue);
        tElem.appendChild (text);
		
		// Set the attributes of the <text> element
        //tElem.setAttributeNS (null, "font-family", fontFamily);
               
        tElem.setAttributeNS (null, "font-size", fontSize);
		// Notice that we set the font color here
        tElem.setAttributeNS (null, "fill", fillColor);
		// set id of new text element
        tElem.setAttributeNS(null, "id", nodeId);
        
        if (textAlignment != null) {
    		// set text alignment (e.g. center)
            tElem.setAttributeNS(null, "text-anchor", textAlignment);
        }
		 
        return tElem;
    }
    
    public void writeToFile() {
        
        try {
        	// Create the necessary IO facilities
        	FileWriter f = new FileWriter("logodemo.svg");
        	PrintWriter writer = new PrintWriter("logodemo.svg", "UTF-8");
        	
        	// add header
        	writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        	writer.write ("<!DOCTYPE svg PUBLIC '");
        	writer.write (SVGConstants.SVG_PUBLIC_ID);
        	writer.write ("' '");
        	writer.write (SVGConstants.SVG_SYSTEM_ID);
        	writer.write ("'>\n\n");
        	
        	// Get the created document from the canvas
        	SVGDocument svgDoc = canvas.getSVGDocument();
        	// And here our SVG content is actually being
        	// written to a file
        	DOMUtilities.writeDocument(svgDoc, writer);
        	writer.close();
        } 
        catch (IOException ioe) {
        	System.err.println("IO problem: " + ioe.toString());
        	ioe.printStackTrace();
        }
        
        /**
    	 // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        try {
        	//Writer out = new OutputStreamWriter(new FileOutputStream(new File("logodemo.svg")), "UTF-8");
        	//boolean useCSS = true; // we want to use CSS style attributes
            //svgGenerator.stream(out, useCSS);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        **/
        
    }
    
    public void start() {       
    	// Display the document.
        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        canvas.setDocument(doc);
        
    }

    public void stop() {
        // Remove the document.
        canvas.setDocument(null);
    }

    public void destroy() {
        canvas.dispose();
    }
    
    
    
    /**
     * Setup and initialize the GUI.  
     */
    private void guiInit() { 
    	// Set the applet to use flow layout.
    	JPanel mainPanel = new JPanel();
    	JPanel headerPanel = new JPanel();    	
        canvas = new MyJSVGCanvas();    
        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
    	jbtnUpdate = new JButton("Aktualisieren");
    	jbtnSave = new JButton("Speichern");
    	jlHeadline = new JLabel("Headline");
    	jlDownline = new JLabel("Downline");
    	jlStatus = new JLabel("");
    	jtfHeadline = new JTextField("Headline");
    	jtfDownline = new JTextField("Downline");
    	
    	jtfHeadline.setPreferredSize(new Dimension(150, 20));
    	jtfDownline.setPreferredSize(new Dimension(150, 20));
    	
    	headerPanel.add(jlHeadline);
    	headerPanel.add(jtfHeadline);
    	
    	headerPanel.add(jlDownline);
    	headerPanel.add(jtfDownline);
    	
    	
    	headerPanel.add(jbtnUpdate);
    	headerPanel.add(jbtnSave);
    	headerPanel.setPreferredSize(new Dimension(500, 80));
    	mainPanel.setLayout(new BorderLayout());
    	mainPanel.add(BorderLayout.NORTH, headerPanel);
    	mainPanel.add(BorderLayout.CENTER, canvas);
    	jlStatus.setHorizontalAlignment(SwingConstants.CENTER);
    	mainPanel.add(BorderLayout.SOUTH, jlStatus);
    	this.setContentPane(mainPanel);
    	
    	
    	// Add action listeners for the buttons. 
    	jbtnUpdate.addActionListener(new ActionListener() {      
        public void actionPerformed(ActionEvent le) {
        	
			canvas.getUpdateManager().getUpdateRunnableQueue().invokeLater
			(new Runnable() {
				
			    public void run() {   
			    	
			    	String headline = jtfHeadline.getText();
			    	String downline = jtfDownline.getText();
			    	
			    	if (headline != null && downline.length() > 0) {
			    		jlStatus.setVisible(false);                	
			    			paintStuff(headline, downline);
			    			  
			    	}
			    		
			    	else {
			    		jlStatus.setText("Bitte Firmenname eingeben");
			    		jlStatus.setVisible(true);
			    	}
			    }
			});
        }        
      
      });
    
    	 
      jbtnSave.addActionListener(new ActionListener() {      
        public void actionPerformed(ActionEvent le) {
        	writeToFile();
        }      
      }); 
             
    }
}
