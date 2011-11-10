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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import gda.observable.IObservable;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.ISidePlotView;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEventListener;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEventListener;

/**
 * Generic interface for Plotting UI attached to different Plotters
 */
public interface IPlotUI extends IObservable, PlotActionEventListener, AreaSelectEventListener {

	/**
	 * Process a plot update
	 * 
	 * @param dbPlot DataBean containing the new plot
	 * @param isUpdate is this an update of an existing plot?
	 */
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate);

	/**
	 * Unregister and dispose all overlays associated to the current plot,
	 * since PlotUI is currently the master on them
	 */
	public void disposeOverlays();

	/**
	 * Deactivate the UI this can be used to do some additional actions before
	 * the UI gets removed
	 */
	public void deactivate(boolean leaveSidePlotOpen);

	/**
	 * @param guiBean
	 */
	public void processGUIUpdate(GuiBean guiBean);

	/**
	 * @return a side plot view
	 */
	public ISidePlotView getSidePlotView();

	/**
	 * Initialise side plot view
	 */
	public ISidePlotView initSidePlotView();

	/**
	 * Called when ui is no longer needed
	 */
	public void dispose();

}
