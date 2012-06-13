package demo;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;

/**
 * subclass of JSVG Canvas is needed in order to access
 * the protected variable bridgeContext which is required
 * for some calculcations (positions and size of objects)
 * 
 * @author cgrafena
 *
 */
public class MyJSVGCanvas extends JSVGCanvas implements GVTTreeRendererListener  {

	public BridgeContext getBridgeContext() {
		return this.bridgeContext;
	}
	public MyJSVGCanvas() {
		setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		addGVTTreeRendererListener(this);
	}
	@Override
	public void gvtRenderingCancelled(GVTTreeRendererEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void gvtRenderingCompleted(GVTTreeRendererEvent arg0) {
		//System.out.println("gvt rendering completed");
		
	}
	@Override
	public void gvtRenderingFailed(GVTTreeRendererEvent arg0) {
		//System.out.println("gvt rendering failed");
		
	}
	@Override
	public void gvtRenderingPrepare(GVTTreeRendererEvent arg0) {
		//System.out.println("gvt rendering prepare");
		
	}
	@Override
	public void gvtRenderingStarted(GVTTreeRendererEvent arg0) {
		//System.out.println("gvt rendering started");
		
	}

}
