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
 *
 */
public class DataPositionEvent extends EventObject implements IDataPositionEvent {

	protected double position[];
	protected Mode currentMode;
	
	/**
	 * Constructor of a DataPositionEvent 
	 * @param instigater Who is creating this event object?
	 * @param position position coordinates in dataSet space
	 * @param mode current mode (start, drag, end)
	 */
	public DataPositionEvent(Object instigater,
            				 double[] position, 
            				 Mode mode) {
		super(instigater);
		this.position = position.clone();
		this.currentMode = mode;
	}

	/**
	 * Get the current mode
	 * 
	 * @return current mode {@link IDataPositionEvent.Mode}
	 */
	@Override
	public Mode getMode()
	{
		return currentMode;
	}
	
	/**
	 * Get the position in texture coordinates
	 * @return texture coordinates
	 */
	@Override
	public double[] getPosition()
	{
		return position;
	}	
}
