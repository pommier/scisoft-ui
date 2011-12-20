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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import java.util.List;

import org.eclipse.jface.action.Action;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;


/**
 * This interface identifies a view that will contain
 * instances of <code>ISidePlot</code>
 * <p>
 * View clients should implement this interface if they intend
 * to host the SidePlot composites
 */
public interface ISidePlotView {

	/**
	 * 
	 * @return the active plot or null if none active.
	 */
	public ISidePlot getActivePlot();
	
	/**
	 * Switch the side plot selected by the index to the front
	 * of the view 
	 * 
	 * @param plotUI UI for plotting
	 * @param index index identifying the selected side plot
	 */
	void switchSidePlot(IPlotUI plotUI, int index);

	/**
	 * Create a list of actions for toolbar
	 * @param plotUI
	 * @return list of Actions
	 */
	public List<Action> createSwitchActions(IPlotUI plotUI);

	/**
	 * Set list of actions
	 * @param actions
	 */
	public void setSwitchActions(List<Action> actions);
	
}
