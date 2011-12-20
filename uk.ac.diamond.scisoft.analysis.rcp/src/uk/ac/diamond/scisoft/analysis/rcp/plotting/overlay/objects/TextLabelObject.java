/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.objects;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay1DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.enums.LabelOrientation;

/**
 *
 */
public class TextLabelObject extends OverlayObject {

	private double cx,cy;
	
	public TextLabelObject(int primID, OverlayProvider provider) {
		super(primID, provider);
	}
	
	public void setTextPosition(double cx, double cy) {
		this.cx = cx;
		this.cy = cy;
	}

	public void setTextOrientation(LabelOrientation orient) {
		if (provider != null && primID != -1)
			provider.setLabelOrientation(primID, orient);
	}
	
	public void setText(String text, int alignment) {
		if (provider != null && primID != -1)
			provider.setLabelText(primID, text, alignment);
	}
	
	public void setTextFont(java.awt.Font font) {
		if (provider != null && primID != -1)
			provider.setLabelFont(primID, font);
	}

	@Override
	public void draw() {
		if (provider instanceof Overlay1DProvider)
			((Overlay1DProvider)provider).drawLabel(primID, cx, cy);
		else if (provider instanceof Overlay2DProvider)
			((Overlay2DProvider)provider).drawLabel(primID, cx, cy);
	}		
}

