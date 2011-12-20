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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IGuiInfoManager;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IMainPlot;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;

/**
 * A side plot control to populate the tab folder
 */
abstract public class SidePlot implements ISidePlot {
	
	protected IGuiInfoManager guiUpdateManager;
	protected IMainPlot       mainPlotter;
	protected IPlotUI         mainPlotUI;
	
	protected long            updateInterval = 0;
	protected long            nextTime = 0;
	protected Composite       container;
	protected boolean         isDisposed = true;

	/**
	 */
	public SidePlot() {
	}
	
	@Override
	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
		nextTime = System.currentTimeMillis() + updateInterval;
	}

	@Override
	public void setGuiInfoManager(IGuiInfoManager guiInfoManager) {
		this.guiUpdateManager = guiInfoManager;
	}

	@Override
	public void setMainPlotter(IMainPlot mainPlotter) {
		this.mainPlotter = mainPlotter;
	}

	@Override
	public IMainPlot getMainPlotter() {
		return mainPlotter;
	}

	@Override
	public void processHistogramUpdate(HistogramUpdate update) {
	}

	/**
	 * By default, this implementation creates an action box. You need to override and call the superclass
	 * method and then decorate the returned action with text, tool-tip text and icon
	 */
	@Override
	public Action createSwitchAction(final int index, final IPlotUI plotUI) {
		Action action = new Action("", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				plotUI.getSidePlotView().switchSidePlot(plotUI, index);
				if (mainPlotter instanceof DataSetPlotter) // TODO decide whether this is necessary
					((DataSetPlotter) mainPlotter).getComposite().setFocus();
			}
		};
		return action;
	}

	@Override
	public Control getControl() {
		return container;
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	public void setDisposed(boolean isDisposed) {
		this.isDisposed = isDisposed;
	}

	/**
	 * If this method is overridden in a subclass, that subclass method <b>must</b> call
	 * its super class method for side plots to operate correctly.
	 * @see ISidePlot#dispose()
	 */
	@Override
	public void dispose() {
		guiUpdateManager = null;
		mainPlotter      = null;
		mainPlotUI       = null;
		if (container != null && !container.isDisposed()) {
			container.dispose();
		}
		container = null;
		isDisposed = true;
	}

	@Override
	public void setMainPlotUI(IPlotUI plotUI) {
		this.mainPlotUI = plotUI;
	}

	protected IPlotUI getMainPlotUI() {
		return mainPlotUI;
	}
}
