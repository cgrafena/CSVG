/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import org.apache.batik.anim.AnimationEvent;

/**
 * Bridge class for the &lt;desc&gt; element.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id: SVGDescElementBridge.java 475477 2006-11-15 22:44:28Z cam $
 */
public class SVGDescElementBridge extends SVGDescriptiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;desc&gt; element.
     */
    public SVGDescElementBridge() {}

    /**
     * Returns 'desc'.
     */
    public String getLocalName() {
        return SVG_DESC_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() { return new SVGDescElementBridge(); }

	@Override
	public void handleAnimationEvent(AnimationEvent evt) {
		// TODO Auto-generated method stub
		
	}
}