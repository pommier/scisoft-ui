/*-
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.ISidePlotView;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionComplexEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEvent;
import uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView;


/**
 * A very general UI for 1D Plots using SWT / Eclipse RCP
 * 
 * With complete action set in the toolbar.
 */
public class Plotting1DUIComplete extends Plotting1DUIAdapter {

	/**
	 * Status item ID
	 */
	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.Plotting1DUI";
	private final static String STATUSSTRING = "Pos: ";
	
	
	private StatusLineContributionItem statusLine;	
	private AxisValues xAxis;
	private IWorkbenchPage page;
	private String plotViewID;
	private PlotWindow plotWindow;
	private List<Action> switchToTabs;
	private AbstractPlottingSystem plottingSystem;
	
	/**
	 * Constructor of a Plot1DUI 
	 * @param window Plot window
	 * @param plottingSystem plotting system
	 * @param parent parent composite 
	 * @param page workbench page
	 * @param viewName name of the view associated to this UI
	 */
	
	public Plotting1DUIComplete(final PlotWindow window,
							AbstractPlottingSystem plottingSystem, 
							Composite parent, 
							IWorkbenchPage page,
							String viewName) {
		super(window.getPlottingSystem(), parent);
		this.page = page;
		this.plotWindow = window;
		this.plotViewID = viewName;
		this.xAxis = new AxisValues();
		this.plottingSystem = plottingSystem;
	}

	@Override
	public ISidePlotView initSidePlotView() {
		try {
			SidePlotView spv = getSidePlotView();
			spv.setSwitchActions(switchToTabs);
			return spv;
		} catch (IllegalStateException ex) {
			logger.debug("Cannot initiate side plot view", ex);
		}
		return null;
	}

	@Override
	public SidePlotView getSidePlotView() {
		return SidePlotUtils.getSidePlotView(page, plotViewID);
	}

	@Override
	public void deactivate(boolean leaveSidePlotOpen) {
		super.deactivate(leaveSidePlotOpen);
		try {
			getSidePlotView().deactivate(leaveSidePlotOpen);
		} catch (IllegalStateException ex) {
		} catch (NullPointerException ne) {
		}
	}

	/**
	 * 
	 * @param manager
	 */
	@Override
	public void buildStatusLineItems(IStatusLineManager manager)
	{
		statusLine = new StatusLineContributionItem(STATUSITEMID);
		statusLine.setText(STATUSSTRING);
		manager.add(statusLine);
	}
	
	@Override
	public void plotActionPerformed(final PlotActionEvent event) {
		if (event instanceof PlotActionComplexEvent)
		{
			parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					PlotDataTableDialog dataDialog = 
						new PlotDataTableDialog(parent.getShell(),(PlotActionComplexEvent)event);
					dataDialog.open();								
				}
			});
		} else
		{
			parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					String pos;
					if (event.getPosition().length > 2) {
						pos = String.format("%s %g (%g):%g", STATUSSTRING, event.getPosition()[0], event.getPosition()[2], event.getPosition()[1]);
					} else {
						pos = String.format("%s %g:%g", STATUSSTRING, event.getPosition()[0], event.getPosition()[1]);
					}
					statusLine.setText(pos);	
				}
			});
		}
		super.plotActionPerformed(event);
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate) {
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<AbstractDataset> datasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());

			xAxis.clear();
			AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS);

			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				datasets.add(data);
			}
			if(xAxisValues != null){
				plottingSystem.reset();
				
				plottingSystem.createPlot1D(xAxisValues, datasets, null);
			}
			parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					plottingSystem.repaint();
					getSidePlotView().processPlotUpdate();
					plotWindow.notifyUpdateFinished();
				}
			});
		}
	}
}
