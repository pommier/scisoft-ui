/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
