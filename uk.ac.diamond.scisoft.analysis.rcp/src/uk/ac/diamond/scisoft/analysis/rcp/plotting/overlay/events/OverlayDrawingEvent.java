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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.events;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEvent;

/**
 * A simple event to wrap data involved in user making a selection
 * on the graph.
 */
public class OverlayDrawingEvent extends GraphSelectionEvent {

	private OverlayProvider provider;
	private int[]           parts;
	private boolean         isInitialDraw;

	/**
	 * @param provider
	 * @param parts
	 */
	protected OverlayDrawingEvent(OverlayProvider provider, int[] parts) {
		super(provider);
		this.provider = provider;
		this.parts    = parts;
		this.isInitialDraw=true;
	}

	/**
	 * @param provider
	 * @param start
	 * @param end
	 * @param parts
	 */
	protected OverlayDrawingEvent(OverlayProvider provider, 
			                      AreaSelectEvent start,
			                      AreaSelectEvent end, 
			                      int[]           parts) {
		super(provider);
		this.provider = provider;
		this.parts    = parts;
		this.start    = start;
		this.end      = end;
		this.isInitialDraw=false;
	}

	
	/**
	 * @return Returns the provider.
	 */
	public OverlayProvider getProvider() {
		return provider;
	}

	/**
	 * @param provider The provider to set.
	 */
	public void setProvider(OverlayProvider provider) {
		this.provider = provider;
	}

	/**
	 * @return Returns the parts.
	 */
	public int[] getParts() {
		return parts;
	}

	/**
	 * @param parts The parts to set.
	 */
	public void setParts(int[] parts) {
		this.parts = parts;
	}

	/**
	 * @return Returns the isInitialDraw.
	 */
	public boolean isInitialDraw() {
		return isInitialDraw;
	}

	/**
	 * @param isInitialDraw The isInitialDraw to set.
	 */
	public void setInitialDraw(boolean isInitialDraw) {
		this.isInitialDraw = isInitialDraw;
	}

}
