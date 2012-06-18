package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGTextElement;


 
/**
 * CSVG demo
 *
 * @author cgrafena
 */
public class Main {

	private static final long serialVersionUID = -9072664017480216697L;	
	private MyJSVGCanvas canvas = new MyJSVGCanvas();
    private SVGDocument doc;
    private JFrame frame;
        
    protected String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    private static final int BASE_FONT_SIZE = 50;

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
    
    public static void main(String[] args) {
    	new Main();
    }
    
   public Main() {
	   initSVGDocument();
	   guiInit();
    }
    
    public void initSVGDocument() {
    	  // Create an SVG document.
    	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();            
     	doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
     	
     	// create canvas and config it
     	//canvas = new MyJSVGCanvas();    
     	     	
    	// init svg and css dom
    	userAgent = new UserAgentAdapter();
        loader    = new DocumentLoader(userAgent);
        
        ctx       = new BridgeContext(userAgent, loader, canvas);
        ctx.setDynamicState(BridgeContext.DYNAMIC);
        builder   = new GVTBuilder();
        rootGN    = builder.build(ctx, doc);
        
        canvas.setSVGDocument(doc);
        canvas.setSize(500, 400);

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
		    		
   		Element headline = createTextNode(sHeadline, "headline", "Arial", String.valueOf(BASE_FONT_SIZE), "black", "middle");
   		Element cHeadlineX = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cHeadlineX.setAttributeNS(null, "attributeName", "x");
   		cHeadlineX.setAttributeNS(null, "value", "c:width(c:viewport()) div 2");
   		
   		Element cHeadlineY = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cHeadlineY.setAttributeNS(null, "attributeName", "y");
   		cHeadlineY.setAttributeNS(null, "value", "c:height(c:viewport()) div 2");
   		
   		headline.appendChild(cHeadlineX);
   		headline.appendChild(cHeadlineY);
   		
   		Element sideline = createTextNode("Sideline", "sideline", "Arial", "10", "black", null);
   		Element cSidelineX = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cSidelineX.setAttributeNS(null, "attributeName", "x");
   		cSidelineX.setAttributeNS(null, "value", "c:x(c:bbox(id('headline'))) + c:width(c:bbox(id('headline')))");
   		
   		Element cSidelineY = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cSidelineY.setAttributeNS(null, "attributeName", "y");
   		
   		cSidelineY.setAttributeNS(null, "value", "c:y(c:bbox(id('headline'))) + c:height(c:bbox(id('headline')))");
   		
   		
   		
   		Element downline = createTextNode("Downline", "downline", "Arial", "10", "black", "middle");
   		Element cDownlineX = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cDownlineX.setAttributeNS(null, "attributeName", "x");
   		cDownlineX.setAttributeNS(null, "value", "c:x(c:bbox(id('headline'))) + ((c:width(c:bbox(id('headline')))) div 2)");
   		
   		Element cDownlineY = doc.createElementNS("http://mcc.id.au/2004/csvg", "c:constraint");
   		cDownlineY.setAttributeNS(null, "attributeName", "y");
   		cDownlineY.setAttributeNS(null, "value", "c:y(c:bbox(id('headline'))) + c:height(c:bbox(id('headline'))) + c:height(c:bbox(id('downline'))) + 10");
   		
   		downline.appendChild(cDownlineX);
   		downline.appendChild(cDownlineY);
   		
   		sideline.appendChild(cSidelineX);
   		sideline.appendChild(cSidelineY);
   		
   		doc.getRootElement().appendChild(headline);
   		doc.getRootElement().appendChild(sideline);
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
        tElem.setAttributeNS (null, "font-family", fontFamily);
               
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
   
    
    /**
     * Setup and initialize the GUI.  
     */
    private void guiInit() { 
    	
    	
    	JPanel mainPanel = new JPanel();
    	JPanel headerPanel = new JPanel();
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
    	
    	// frame settings
    	frame = new JFrame();
    	
    	frame.getContentPane().add(mainPanel);
    	frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    	
    	frame.setSize(500, 500);
    	
    	frame.setVisible(true);
    	
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
			    		jlStatus.setText("Please insert Headline & Downline");
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
