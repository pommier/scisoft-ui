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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.tools;

import java.util.List;

public interface IImagePositionEvent extends IDataPositionEvent{

	/**
	 * Bit mask for left mouse button
	 */
	public static final short LEFTMOUSEBUTTON = 1;

	/**
	 * Bit mask for right mouse button
	 */
	public static final short RIGHTMOUSEBUTTON = 2;

	/**
	 * Bit mask for the control key
	 */
	public static final short CTRLKEY = 4;

	/**
	 * Bit mask for the shift key
	 */
	public static final short SHIFTKEY = 8;

	/**
	 * Get the id of the overlay primitive that the mouse is currently add
	 * @return -1 if there is no overlay primitive otherwise its id
	 */
	public abstract int getPrimitiveID();

	/**
	 * Get the ids of the overlay primitive that the mouse is currently on
	 * @return an array, possibly empty, of the primitives that were hit
	 */
	public abstract List<Integer> getPrimitiveIDs();

	/**
	 * // TODO This really should return a double, ie if you can draw on a double (See OverlayProviders) 
	 * // you can also also click on a double
	 * Get the position in the image in pixel coordinates
	 * @return image position
	 */
	public abstract int[] getImagePosition();

	/**
	 * Get the specific bit flags
	 * @return the bit flags
	 */
	public abstract short getFlags();

}
