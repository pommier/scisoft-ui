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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.dawb.common.ui.plot.region.IRegion;
import org.dawb.common.ui.plot.region.IRegionBoundsListener;
import org.dawb.common.ui.plot.region.ROIEvent;
import org.dawb.common.ui.plot.region.RegionBounds;
import org.dawb.common.ui.plot.trace.IImageTrace;
import org.dawb.common.ui.plot.trace.ITrace;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;

/**
 * Class to create the a 2D/image plotting
 */
public class Plotting2DUI extends AbstractPlotUI implements IRegionBoundsListener {

	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.Plotting2DUI";

	private AbstractPlottingSystem plottingSystem;

	private List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());

	private static final Logger logger = LoggerFactory.getLogger(Plotting2DUI.class);

	/**
	 * @param plotter
	 */
	public Plotting2DUI(final AbstractPlottingSystem plotter){
		this.plottingSystem = plotter;
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate)
	{
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<AbstractDataset> yDatasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());

			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				yDatasets.add(data);
			}

			final AbstractDataset data = yDatasets.get(0);
			if(data != null){
				data.setName("");

				final Collection<ITrace> traces = plottingSystem.getTraces(IImageTrace.class);
				if (traces!=null && traces.size()>0) {
					final IImageTrace image = (IImageTrace)traces.iterator().next();
					final int[]       shape = image.getData().getShape();
					if (Arrays.equals(shape, data.getShape())) {
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								// This will keep the previous zoom level if there was one
								// and will be faster than createPlot2D(...) which autoscales.
								image.setData(data, image.getAxes(), false);
							}
						});
					} else {
						plottingSystem.createPlot2D(data, null, null);
					}
				} else {
					plottingSystem.createPlot2D(data, null, null);
				}
				logger.debug("Plot 2D created");
			}
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
		updateGUI(guiBean);
	}


	public void updateGUI(GuiBean guiBean) {
		Collection<IRegion> regions = plottingSystem.getRegions();
		for (Iterator<IRegion> iterator = regions.iterator(); iterator.hasNext();) {
			IRegion iRegion = iterator.next();
			guiBean.put(GuiParameters.ROIDATA, createRegion(iRegion));
			logger.debug("ROI x:"+ iRegion.getROI().getPointX()+" ROI y:"+iRegion.getROI().getPointY());
		}
	}

	private ROIBase createRegion(IRegion iRegion) {
		return iRegion.getROI();
	}

	@Override
	public void roiDragged(ROIEvent evt) {
//		update((IRegion)evt.getSource(), evt.getRegionBounds());
	}

	@Override
	public void roiChanged(ROIEvent evt) {
//		final IRegion region = (IRegion)evt.getSource();
//		update(region, region.getRegionBounds());
//		
//		try {
//			updateProfiles.join();
//		} catch (InterruptedException e) {
//			logger.error("Update profiles job interrupted!", e);
//		}
//		
//		getControl().getDisplay().syncExec(new Runnable() {
//			public void run() {
//				plotter.autoscaleAxes();
//			}
//		});
	}
	
//	private synchronized void update(IRegion r, RegionBounds rb) {
//		if (r!=null && !isRegionTypeSupported(r.getRegionType())) return; // Nothing to do.
//		if (isUpdateRunning)  updateProfiles.cancel();
//		this.currentRegion = r;
//		this.currentBounds = rb;
//		updateProfiles.schedule();
//	}
}
