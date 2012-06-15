/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.objects;

import org.eclipse.swt.graphics.Image;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;

public class ImageObject extends OverlayObject {

	private Image image;
	private double lux, luy, rlx, rly;
	
	public ImageObject(int primID, OverlayProvider provider) {
		super(primID, provider);
	}

	public void setUpperLeftPoint(double lux, double luy) {
		this.lux = lux;
		this.luy = luy;
	}
	
	public void setBottomRightPoint(double rlx, double rly) {
		this.rlx = rlx;
		this.rly = rly;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	@Override
	public void draw() {
		if (provider instanceof Overlay2DProvider)
			((Overlay2DProvider)provider).drawImage(primID, image, lux, luy, rlx, rly);
	}		
}