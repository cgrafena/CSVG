package org.apache.batik.anim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.anim.values.AnimatableIntegerValue;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.dom.anim.AnimationTarget;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * This class handles animation and other dynamic value changes
 * and updates the document appropriately.
 *
 * @author <a href="mailto:cam@mcc.id.au">Cameron McCormack</a>
 * @version $Id: AnimationEngine.java,v 1.14 2004/02/12 02:48:31 clm Exp $
 */
public class ConstraintsAnimationEngine {
    //
    // The attribute type values.
    //
    public static final short ATTRIBUTE_TYPE_XML = 0;
    public static final short ATTRIBUTE_TYPE_CSS = 1;
    public static final short ATTRIBUTE_TYPE_AUTO = 2;

    /**
     * The document being animated.
     */
    Document document;

    /**
     * The animation context object.
     */
    BridgeContext animationContext;

    /**
     * The event listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The animations.
     */
    protected List animations = Collections.synchronizedList(new LinkedList());

    /**
     * The animations in a table.
     */
    protected DoublyIndexedTable animationsMap = new DoublyIndexedTable();

    /**
     * Animations which have notified the engine that they need to be updated.
     */
    protected List pendingAnimations = Collections.synchronizedList(new LinkedList());

    /**
     * Event handler for SVG onload event.
     */
    protected SVGLoadHandler svgLoadHandler;

    /**
     * The syncbase of the document (that is, the time at which the
     * SVGLoad event was fired).
     */
    protected Date syncbase;

    /**
     * The current time at the last animation tick.
     */
    protected Date currentTime;

    /**
     * The thread that handles clock ticks.
     */
    protected ClockThread clockThread;

    /**
     * Whether the engine will wait for a signal from an Animation
     * instead of polling all Animations.
     */
    protected boolean sleeping = false;

    /**
     * A list of the absolute begin times of all the animations.
     * This is so the animation engine can wake itself up when
     * an animation is about to begin.
     */
    protected ArrayList absoluteBeginTimes = new ArrayList();
    
    List listenerList = Collections.synchronizedList(new LinkedList());
    List eventList = Collections.synchronizedList(new LinkedList());
    
    /**
     * Creates a new AnimationEngine.
     */
    public ConstraintsAnimationEngine(Document doc, BridgeContext ctx) {
        document = doc;
        animationContext = ctx;
        EventTarget et = (EventTarget) document.getDocumentElement();
        svgLoadHandler = new SVGLoadHandler();
        et.addEventListener("SVGLoad", svgLoadHandler, false);
        clockThread = new ClockThread();
        clockThread.setPriority(clockThread.getThreadGroup().getMaxPriority());
    }
    
    /**
     * Disposes the AnimationEngine.
     */
    public void dispose() {
    	clearAll();
        EventTarget et = (EventTarget) document.getDocumentElement();
        et.removeEventListener("SVGLoad", svgLoadHandler, false);
        clockThread.interrupt();        
    }
    
    public void clearAll() {
    	animations.clear();
    	listeners.clear();
    }
    
    /**
     * Adds an animation event listener.
     */
    public void addEventListener(AnimationEventListener l) {
        listeners.add(l);
    }

    /**
     * Removes an animation event listener.
     */
    public void removeEventListener(AnimationEventListener l) {
        listeners.remove(l);
    }

    protected final static AnimationEventListener[] LISTENER_ARRAY =
        new AnimationEventListener[0];

    /**
     * Fires a synthesised animation event.
     */
    public void simulateAnimationEvent(Element e, String pn, int pt, String value) {
        AnimationEventListener[] ll =
            (AnimationEventListener[]) listeners.toArray(LISTENER_ARRAY);

        int len = ll.length;
        if (len > 0) {
            AnimationEvent evt = new AnimationEvent(this, e, pn, pt, value);
            for (int i = 0; i < len; i++) {
                ll[i].animatedValueUpdated(evt);
            }
        }
    }

    /**
     * Add an animation to the engine.
     */
    public void addAnimation(Animation a) {
    	//System.out.println("addAnimation() for element " + a.getElement() + " attribute" + a.getAttributeName() + " value: " + a.getValue());
        animations.add(a);
        Element e = a.getElement();
        String an = a.getAttributeName();
        List al = (List) animationsMap.get(e, an);
        if (al == null) {
            al = new LinkedList();
            animationsMap.put(e, an, al);
        }
        al.add(a);

        double[] t = a.getAbsoluteBeginTimes();
        int s = absoluteBeginTimes.size();
        absoluteBeginTimes.ensureCapacity(s + t.length);
        for (int i = 0; i < t.length; i++) {
            int j = 0;
            while (s > j && ((Double) absoluteBeginTimes.get(j)).doubleValue() < t[i]) {
                j++;
            }
            absoluteBeginTimes.add(j, new Double(t[i]));
            s++;
        }
    }

    /**
     * Get the animation for a particular attribute.
     */
    public List getAnimations(Element e, String attrName) {
        List al = (List) animationsMap.get(e, attrName);
        if (al == null) {
            return null;
        }
        return Collections.unmodifiableList(al);
    }

    /**
     * To handle the SVGLoad event on the document.
     */
    protected class SVGLoadHandler implements EventListener {
        public void handleEvent(Event evt) {
            syncbase = Calendar.getInstance().getTime();
            /*Iterator i = animations.iterator();
            while (i.hasNext()) {
                Animation a = (Animation) i.next();
                if (!a.isActive()) {
                    // this is an "interrupt" animation, so we'll update
                    // these values initially
                    queuePendingAnimation(a);
                }
            }*/
            //System.out.println("thread state: " + clockThread.getState().toString());
            
            clockThread = null;
            clockThread = new ClockThread();
            clockThread.start();
            // System.out.println("-- Document starts: " + getSyncbaseTime());
        }
    }

    /**
     * Returns the current offset from the syncbase time.
     */
    public double getSyncbaseTime() {
        if (syncbase == null || currentTime == null) {
            return 0.0;
        }
        return (currentTime.getTime() - syncbase.getTime()) / 1000.0;
    }

    /**
     * Forces the animation engine to check each animation for
     * updates.
     */
    public void wake() {
        if (sleeping) {
            clockThread.wake();
        } else {
            // System.out.println("Waking clock thread but it's already awake");
        }
    }
    
    /**
     * Queue an animation to be updated.
     */
    protected void queuePendingAnimation(Animation a) {
        synchronized (pendingAnimations) {
        	
        	//System.out.println("queue animation " + a + " with value " + a.getValue());
        	
        
        	if (!pendingAnimations.contains(a)) {                 
                pendingAnimations.add(pendingAnimations.size(), a); // add animation to the end of queue
                //pendingAnimations.addLast(a);
            } else {
                // System.out.println("Would queue animation, but already pending: " + a);
                // pendingAnimations.remove(a);
                // pendingAnimations.addLast(a);
            }
        	
        	/*for (int z = 0; z < pendingAnimations.size(); z++) {
        		System.out.println("animation pending: " + a + " value: " + a.getValue());
        	}*/
        }
        //System.out.println("queue animation for element " + a.getElement() + " attribute" + a.getAttributeName() + " value: " + a.getValue());
    }
    
    /**
     * Get the next pending animation.
     */
    protected Animation getPendingAnimation() {
        synchronized (pendingAnimations) {
            if (pendingAnimations.isEmpty()) {
                // System.out.println("No more pending animations to get");
                // System.out.println("No remaining animations: " + getSyncbaseTime());
                return null;
            }
            
            /*for (int j = 0; j < pendingAnimations.size(); j++) {
            	System.out.println("pending animation: " + pendingAnimations.get(j) + " value: " +  ((Animation)pendingAnimations.get(j)).cachedValue);
            }*/
    
            // System.out.println("Getting pending animation: " + (Animation) pendingAnimations.getFirst());
            //return (Animation) pendingAnimations.removeFirst();
            return (Animation) pendingAnimations.remove(0); // remove first
        }
    }
    
    /**
     * Queue an number of animations to be updated.
     */
    public void queuePendingAnimations(Animation[] anims) {    	    	
    	for (int i = 0; i < anims.length; i++) {        	
            queuePendingAnimation(anims[i]);
        }
        wake();
    }
    
    /**
     * Updates all animations.
     */
    protected synchronized void tick() {
        double t = getSyncbaseTime();
        // System.out.println("Tick: " + t);
        while (absoluteBeginTimes.size() > 0 
                && ((Double) absoluteBeginTimes.get(0)).doubleValue() <= t) {
            absoluteBeginTimes.remove(0);
        }

       

        Object[] anis = animations.toArray();
        // Iterator i = animations.iterator();
        int i = 0;
        boolean anyActive = false;
        Animation a;
        
    	listenerList = new LinkedList();
    	eventList = new LinkedList();
    	
        for (;;) {
            boolean interruptAnimation = false;
            if (i < anis.length/*i.hasNext()*/) {
                // get the next scheduled animation
                a = (Animation) anis[i++];//i.next();
                // System.out.println("Getting next scheduled animation: " + a);
            } else {
                // get the next interrupt animation
                a = getPendingAnimation();
                interruptAnimation = true;
            }
            if (a == null) {
                break;
            }
            
            //System.out.println("before if, animation " + a + " attribute :" + a.getAttributeName() + " value" + a.cachedValue);
            
            if (interruptAnimation || a.isActive()) {               
                anyActive = true;
                //System.out.println("[" + System.currentTimeMillis() + "]" + " before adding animation to animation queue for animation a: " + a);
                String value = a.getValue();
                if (a.hasChanged()) {
                	//System.out.println("[" + System.currentTimeMillis() + "]" + "hasChanged=true before adding animation to animation queue for animation a: " + a + " value: " + a.getValue());
                    Iterator j = listeners.iterator();
                    while (j.hasNext()) {
                        AnimationEventListener l = (AnimationEventListener) j.next();
                        AnimationEvent evt = new AnimationEvent(this,
                                                                a.getElement(), 
                                                                a.getAttributeName(), 
                                                                a.getAttributeType(),
                                                                value);
                        listenerList.add(l);
                        eventList.add(evt);                        
                        //System.out.println("[" + System.currentTimeMillis() + "]" + " added animation for element " + a.getElement() + " attribute " + a.getAttributeName() + " value" + value + " to animation queue");
                    }
                }
                //else
                //	System.out.println("[" + System.currentTimeMillis() + "]" + "Animation " + a + " with value " + a.getValue() + "  has not changed!");
            }            
        }
        //if (listenerList.size() > 0 && eventList.size() > 0) {
        //	globalListenerList.addAll(listenerList);
        //	globalEventList.addAll(eventList);
        //}        
        
        runUpdates();
       
        // Sleep only if there are no active animations
        // and none are set to begin in the next two seconds.
        sleeping = !anyActive
            && (absoluteBeginTimes.size() == 0
                    || ((Double) absoluteBeginTimes.get(0)).doubleValue()
                        >= t + 2);
        if (sleeping) {
            // System.out.println("Going to sleep");
        }
    }
    
    public synchronized void runUpdates() {
    	UpdateManager um = animationContext.mycanvas.getUpdateManager();
    	//System.out.println("update manager in queue updates: " + um);
    	if (um != null) {    
    		
    		final List listenerListCopy = Collections.synchronizedList(new LinkedList(listenerList));
    		final List eventListCopy = Collections.synchronizedList(new LinkedList(eventList));
    		
    		//System.out.println("send " + eventListCopy.size() + " messages to listeners");
    		
    		um.getUpdateRunnableQueue().invokeLater(
			  new Runnable() {
                    public void run() {
                        Iterator i = listenerListCopy.iterator();
                        Iterator j = eventListCopy.iterator();
                        while (i.hasNext()) {
                            AnimationEventListener l = (AnimationEventListener) i.next();
                            AnimationEvent evt = (AnimationEvent) j.next();
                           //System.out.println("[" + System.currentTimeMillis() + "]" + " Firing " + evt.getAttributeName() + " with value " + evt.getNewValue() 
                           	//+ " for element " + evt.getElement().getNodeName() + " (id: " + evt.getElement().getAttribute("id") + ")");
                            l.animatedValueUpdated(evt);
                            System.out.println("Element gets changed" + evt.getElement());
                            evt.getElement().setAttributeNS(null, evt.getAttributeName(), evt.getNewValue());
                            
                            /*for (int z = 0; z< pendingAnimations.size(); z++) {
                                System.out.println("pending animation: " + pendingAnimations.get(z));                            	
                            }*/
                        }                       
                    }
                }
            ); 
    		
    	}
    }
    
    /**
     * This class calls the tick method on the AnimationEngine to cause
     * animation updates as quickly as possible.
     */
    protected class ClockThread extends Thread {
        boolean waking = false;

        public ClockThread() {
        }

        public void run() {
            System.gc();
            while (!interrupted()) {
                currentTime = Calendar.getInstance().getTime();
                if (sleeping) {
                    try {
                        if (absoluteBeginTimes.size() > 0) {
                            double t = ((Double) absoluteBeginTimes.get(0)).doubleValue();
                            double s = getSyncbaseTime();
                            if (t - s < 2) {
                                sleeping = false;
                                continue;
                            }
                        }
                        // System.out.println("Zzzz...");
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        if (waking) {
                            // System.out.println("Woken up");
                            sleeping = false;
                            interrupted();
                        }
                    }
                } else {
                    tick();
                }
            }
        }

        public void wake() {
            // System.out.println("Waking clock thread");
            waking = true;
            interrupt();
        }
    }
}
