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

package uk.ac.diamond.scisoft.analysis.rcp.views.plot;

import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Implement to enable a StaticScanPlot to be constructed from the implementor.
 */
public interface PlotView {

	/**
	 * Implemented to set up the plotter from this class.
	 * Could probably copy default implementation of this from
	 * GDA to Sci-soft and reduce code copying. Will do this as
	 * soon (if) another class extends AbstractPlotView.
	 * 
	 * Not needed to be implemented if plot cannot be saved in a 
	 * static plot.
	 * @return f
	 */
	public PlotBean getPlotBean();

	/**
	 * @return d
	 */
	public String getPartName();

	/**
	 * @return d
	 */
	public IWorkbenchPartSite getSite();

}
