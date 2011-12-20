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


/**
 * An event object that occurs when an Mouse position event is happening
 * inside the Image area
 */
public class ImagePositionEvent extends DataPositionEvent implements IImagePositionEvent {

	private int imagePosition[];
	private int primitiveID;
	private short flags;
	
	/**
	 * Constructor of an ImagePositionEvent
	 * @param tool tool the event has been constructed from
	 * @param position position in the image as texture coordinates (double)
	 * @param imagePosition position in the image as absolute pixel positions (int)
	 * @param primitiveID if the mouse is currently over a primitive its id
	 * @param flags specific bit flags to encode what device buttons are used
	 * @param mode current mode (start, drag, end)
	 */
	public ImagePositionEvent(ImagePositionTool tool, 
			                  double[] position, 
			                  int[] imagePosition,
			                  int primitiveID,
			                  short flags,
			                  Mode mode)
	{
		super(tool,position,mode);
		this.imagePosition = imagePosition.clone();
		this.primitiveID = primitiveID;
		this.flags = flags;
	}
	
	
	/**
	 * Get the id of the overlay primitive that the mouse is currently add
	 * @return -1 if there is no overlay primitive otherwise its id
	 */
	@Override
	public int getPrimitiveID()
	{
		return primitiveID;
	}
	
	/**
	 * Get the position in the image in pixel coordinates
	 * @return image position
	 */
	@Override
	public int[] getImagePosition()
	{
		return imagePosition;
	}
	
	/**
	 * Get the specific bit flags
	 * @return the bit flags
	 */
	@Override
	public short getFlags() {
		return flags;
	}


	@Override
	public List<Integer> getPrimitiveIDs() {
		throw new UnsupportedOperationException("List of IDs is not implemented. Use getPrimitive");
	}

}
