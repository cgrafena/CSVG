package org.apache.batik.anim;

/**
 * Class to be notified of animation updates.
 *
 * @author <a href="mailto:cam@mcc.id.au">Cameron McCormack</a>
 * @version $Id: AnimationEventListener.java,v 1.2 2003/12/15 10:16:15 clm Exp $
 */
public interface AnimationEventListener {

    /**
     * Called when an animated CSS property or XML attribute is updated.
     */
    public void animatedValueUpdated(AnimationEvent evt);
}
