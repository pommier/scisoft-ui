/*-
 * Copyright © 2009 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting.tools;

import java.util.EventObject;

/**
 * AreaSelectEvent is an event object that gets created when an AreaSelect
 * has been started, is in progress or finished via the AreaSelectTool
 */
public class AreaSelectEvent extends EventObject {

	private static final long serialVersionUID = 3001L;
	
	private double position[];
	private int primitiveID;
	private char areaSelectMode;
	
	/**
	 * Create an AreaSelectEvent
	 * @param tool the AreaSelectTool that fires the event
	 * @param position current mouse position in object space
	 * @param mode current mode of the event 0 - start, 1 - ongoing, 2 - finished
	 */
	public AreaSelectEvent(AreaSelectTool tool, 
						   double[] position, 
						   char mode,
						   int primitiveID)
	{
		super(tool);
		this.position = position.clone();
		areaSelectMode = mode;
		this.primitiveID = primitiveID;
	}
	
	/**
	 * Return the current mode
	 * @return current mode 
	 */
	public char getMode()
	{
		return areaSelectMode;
	}
	
	/**
	 * @return the position of the mouse in object space
	 */
	public double[] getPosition()
	{
		return position;
	}	
	
	/**
	 * @return x convenience method
	 */
	public double getX() {
		return position[0];
	}
	/**
	 * @return y convenience method
	 */
	public double getY() {
		return position[1];
	}
	
	public int getPrimitiveID() {
		return primitiveID;
	}
}
