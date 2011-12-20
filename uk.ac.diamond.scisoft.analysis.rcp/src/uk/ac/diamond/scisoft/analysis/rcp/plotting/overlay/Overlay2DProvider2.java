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

import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.PrimitiveType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.objects.OverlayObject;

/**
 *
 */
public interface Overlay2DProvider2 extends Overlay2DProvider {

	/**
	 * Register a PrimitiveType but get as return an OverlayObject
	 * instead of just an integer handle 
	 * @param primType which primitive type to be registed
	 * @return OverlayObject or null if failed
	 */	
	
	public OverlayObject registerObject(PrimitiveType primType);	
	
	public OverlayImage registerOverlayImage(int width, int height);
	
}
