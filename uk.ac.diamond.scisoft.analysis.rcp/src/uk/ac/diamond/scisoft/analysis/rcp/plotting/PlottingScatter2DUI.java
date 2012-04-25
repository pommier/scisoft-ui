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
import org.dawb.common.ui.plot.trace.ILineTrace;
import org.dawb.common.ui.plot.trace.ILineTrace.PointStyle;
import org.dawb.common.ui.plot.trace.ILineTrace.TraceType;
import org.dawb.common.ui.plot.trace.ITrace;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;

/**
 *
 */
public class PlottingScatter2DUI extends AbstractPlotUI {

	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingScatter2DUI";
	private AbstractPlottingSystem plottingSystem;
	private Logger logger = LoggerFactory.getLogger(PlottingScatter2DUI.class);

	public PlottingScatter2DUI(AbstractPlottingSystem plotter) {
		this.plottingSystem = plotter;
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate) {
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();

			final List<AbstractDataset> yDatasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());

			AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS);

			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				yDatasets.add(data);
			}
			plottingSystem.clear();
			Collection<ITrace> traces = plottingSystem.createPlot1D(xAxisValues, yDatasets, null);
			for (ITrace iTrace : traces) {
				final ILineTrace lineTrace = (ILineTrace)iTrace;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						lineTrace.setPointStyle(PointStyle.CROSS);
						lineTrace.setTraceType(TraceType.POINT);
						lineTrace.setPointSize(10);
					}
	
				});
				
			}
			logger.debug("Scatter plot created");
		}
	}
}
