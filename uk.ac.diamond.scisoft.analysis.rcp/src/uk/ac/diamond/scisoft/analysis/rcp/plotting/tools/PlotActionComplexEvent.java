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

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;

/**
 * Complex Plot Action event that not only provides the position on the graph
 * but also gives the underlying dataset and the current region of interest
 */

public class PlotActionComplexEvent extends PlotActionEvent {

	private IDataset associatedData;
	private SelectedWindow window;
	private AxisValues associateXAxis;
	
	/**
	 * @param tool
	 * @param position
	 * @param dataAssoc
	 * @param xAxis 
	 * @param window 
	 */
	public PlotActionComplexEvent(PlotRightClickActionTool tool, double[] position,
								  IDataset dataAssoc, AxisValues xAxis, 
								  SelectedWindow window) {
		super(tool, position);
		this.associatedData = dataAssoc;
		this.window = window;
		this.associateXAxis = xAxis;
	}

	/**
	 * Get the associated data set
	 * @return the associated data set
	 */
	public IDataset getDataSet() {
		return associatedData;
	}
	
	/**
	 * Get the associated x axis values
	 * @return the associated x axis values if available 
	 */
	public AxisValues getAxisValue() {
		return associateXAxis;
	}
	
	/**
	 * Get the current data window / region of interest / zoom level
	 * @return the data window
	 */
	
	public SelectedWindow getDataWindow() {
		return window;
	}
	
}
