/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

import gda.observable.IObserver;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.ISidePlotView;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEvent;


public abstract class AbstractPlotUI implements IPlotUI {

	@Override
	public void deactivate(boolean leaveSidePlotOpen) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void disposeOverlays() {
	}

	@Override
	public ISidePlotView getSidePlotView() {
		return null;
	}

	@Override
	public ISidePlotView initSidePlotView() {
		return null;
	}

	@Override
	public void processGUIUpdate(GuiBean guiBean) {
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate) {
	}

	@Override
	public void areaSelected(AreaSelectEvent event) {
	}

	@Override
	public void plotActionPerformed(PlotActionEvent event) {
	}

	@Override
	public void deleteIObservers() {
	}

	@Override
	public void addIObserver(IObserver observer) {
	}

	@Override
	public void deleteIObserver(IObserver observer) {
	}
}
