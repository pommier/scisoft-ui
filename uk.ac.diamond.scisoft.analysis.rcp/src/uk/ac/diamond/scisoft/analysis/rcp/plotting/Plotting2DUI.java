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

import gda.observable.IObserver;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.ISidePlotView;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;
import uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView;

/**
 *
 */
public class Plotting2DUI extends AbstractPlotUI {

	/**
	 * Status item ID
	 */
	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.Plotting2DUI";
	
	private AbstractPlottingSystem plottingSystem;
	private IWorkbenchPage page;
	private String plotViewID;

	private List<IObserver> observers = 
		Collections.synchronizedList(new LinkedList<IObserver>());

	private PlotWindow plotWindow;
	private HistogramView histogramView;
	private AxisValues xAxis = null;
	private AxisValues yAxis = null;
	private List<Action> switchToTabs;
	//private HistogramDataUpdate histoUpdate = null;
	private static final Logger logger = LoggerFactory.getLogger(Plotting2DUI.class);

	/**
	 * @param window 
	 * @param plotter
	 * @param page 
	 * @param id 
	 */
	public Plotting2DUI(PlotWindow window, 
						final AbstractPlottingSystem plotter, 
						IWorkbenchPage page,
						String id){
		this.plotWindow = window;
		this.plottingSystem = plotter;
		this.page = page;
		this.plotViewID = id;
		xAxis = new AxisValues();
		yAxis = new AxisValues();
	}

	public void initHistogramView(String id) {
		try {
			 histogramView = (HistogramView) page.showView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView",
					id, IWorkbenchPage.VIEW_CREATE);

			 plotWindow.addIObserver(histogramView);
			 histogramView.addIObserver(plotWindow);
		} catch (PartInitException e) {
			logger.error("Failed to initialized histogram View");
		}		
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
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate)
	{
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<AbstractDataset> datasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());
			xAxis.clear();
			yAxis.clear();

			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				datasets.add(data);
			}

			AbstractDataset data = datasets.get(0);
			if(data != null){
				data.setName("");
				plottingSystem.repaint();
				plottingSystem.createPlot2D(data, datasets, null);
			}
		}
	}

	/**
	 * @return the histogram view
	 */
	public HistogramView getHistogramView() {
		return histogramView;
	}

	@Override
	public void addIObserver(IObserver anIObserver) {
		observers.add(anIObserver);
	}

	@Override
	public void deleteIObserver(IObserver anIObserver) {
		observers.remove(anIObserver);
	}

	@Override
	public void deleteIObservers() {
		observers.clear();
	}

	@Override
	public void disposeOverlays() {
		getSidePlotView().disposeOverlays();
	}

//	@Override
//	public void deactivate(boolean leaveSidePlotOpen) {
//		histoUpdate = null;
//		plotWindow.deleteIObserver(histogramView);
//		try {
//			getSidePlotView().deactivate(leaveSidePlotOpen);
//		} catch (IllegalStateException ex) {}
//		try {
//			page.hideView(histogramView);
//		} catch (IllegalArgumentException e) {
//		}
//	}

	@Override
	public void processGUIUpdate(GuiBean guiBean) {
		getSidePlotView().updateGUI(guiBean);
	}

	@Override
	public SidePlotView getSidePlotView() {
		return SidePlotUtils.getSidePlotView(page, plotViewID);
	}
}
