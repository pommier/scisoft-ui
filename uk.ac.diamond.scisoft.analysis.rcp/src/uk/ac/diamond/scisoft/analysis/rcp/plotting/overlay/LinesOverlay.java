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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay;

import java.util.List;

import org.eclipse.swt.widgets.Display;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.OverlayType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.PrimitiveType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.events.AbstractOverlayConsumer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.events.OverlayDrawingEvent;

/**
 * Draws one or more vertical lines at various places in the data.
 */
public class LinesOverlay extends AbstractOverlayConsumer {

	private double[]         xValues;
	private double           min, max;
	private java.awt.Color[] colours;
	
	public LinesOverlay(Display display, final java.awt.Color[] colours) {
		super(display);
		this.colours = colours;
	}

	@Override
	protected int[] createDrawingParts(OverlayProvider provider) {
		final int[] lines = new int[colours.length];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = provider.registerPrimitive(PrimitiveType.LINE);
		}
		return lines;
	}
	

	@Override
	protected void drawOverlay(OverlayDrawingEvent evt) {
		draw();
	}
    private void draw() {
    	for (int i = 0; i < xValues.length; i++) {
            drawLine(xValues[i], colours[i], i);
    	}
   }

	private void drawLine(double x, java.awt.Color color, int partIndex) {
    	provider.begin(OverlayType.VECTOR2D);
    	provider.setColour(parts[partIndex], color);
    	((Overlay1DProvider)provider).drawLine(parts[partIndex], x, min, x, max);		
    	provider.end(OverlayType.VECTOR2D);
	}

    public void setXValues(final double[] xValues) {
    	this.xValues = xValues;
    }

	public void setY(final AbstractDataset y) {
		this.min = y.min().doubleValue();
		this.max = y.min().doubleValue();
	}
	
	public void setYs(List<IDataset> ys) {
		this.min = Double.MAX_VALUE;
		this.max = -Double.MAX_VALUE;
		for (IDataset y : ys) {
			this.min = Math.min(min, y.min().doubleValue());
			this.max = Math.max(max, y.max().doubleValue());
		}
		
	}
}
