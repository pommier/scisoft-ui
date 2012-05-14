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
import org.dawb.common.ui.plot.region.IRegion;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;

//import gda.observable.IObserver;

/**
 * Class to create the a 2D/image plotting
 */
public class Plotting2DUI extends AbstractPlotUI {

	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.Plotting2DUI";

	private AbstractPlottingSystem plottingSystem;
	private List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());
	private PlotWindow plotWindow;
	private AbstractDataset data;
	private static final Logger logger = LoggerFactory.getLogger(Plotting2DUI.class);

	/**
	 * @param plotter
	 */
	public Plotting2DUI(PlotWindow window, final AbstractPlottingSystem plotter){
		this.plotWindow = window;
		this.plottingSystem = plotter;
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate){
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<AbstractDataset> yDatasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());

			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				yDatasets.add(data);
			}

			data = yDatasets.get(0);
			if(data != null){
				data.setName("");
				plottingSystem.updatePlot2D(data, null, null);
				logger.debug("Plot 2D created");
			} else
				logger.debug("No data to plot");
		}
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
	public void processGUIUpdate(GuiBean guiBean) {
		
		final Collection<IRegion> regions = plottingSystem.getRegions();
		final ROIBase currentRoi = (ROIBase)guiBean.get(GuiParameters.ROIDATA);

		logger.debug("There is a guiBean update:"+ guiBean.toString());

		for (final IRegion iRegion : regions) {
			if(plotWindow.currentRoiPair.getName().equals(iRegion.getName())){
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						iRegion.setROI(currentRoi);
					}
				});
			}
		}
	}
}
