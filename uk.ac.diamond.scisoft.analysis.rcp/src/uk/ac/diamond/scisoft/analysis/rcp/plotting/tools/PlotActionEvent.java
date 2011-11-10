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
 * PlotActionEvent. A PlotActionEvent will be either be created when a right click
 * action occurs or just by hovering the mouse over the graph elements
 */

public class PlotActionEvent extends EventObject {

	private double position[];
	private int dataPosition[];
	private int graphNr;
	
	/**
	 * @param tool
	 * @param position
	 */
	public PlotActionEvent(PlotActionTool tool, double[] position) {
		super(tool);
		this.position = position.clone();
		this.graphNr = -1;
	}

	/**
	 * @param tool
	 * @param position
	 * @param graphNr
	 */
	public PlotActionEvent(PlotActionTool tool, double[] position, int graphNr) {
		this(tool,position);
		this.graphNr = graphNr;
	}
	
	/**
	 * Set the actual data position in the underlying Dataset 
	 * @param position the actual data position
	 */
	
	public void setDataPosition(int[] position)
	{
		this.dataPosition = position.clone();
	}
	
	/**
	 * Get the actual data position in the underlying Dataset 
	 * @return the actual data position
	 */
	
	public int[] getDataPosition()
	{
		return dataPosition;		
	}
	/**
	 * Get the selected graph number 
	 * @return the selected graph number if available otherwise -1
	 */
	public int getSelectedGraphNr() {
		return graphNr;
	}

	/**
	 * @return the position of the mouse in object space
	 */
	
	public double[] getPosition()
	{
		return position;
	}	
	
	@Override
	public String toString() {
		return "( "+position[0]+", "+position[1]+" )";
	}
	
}
