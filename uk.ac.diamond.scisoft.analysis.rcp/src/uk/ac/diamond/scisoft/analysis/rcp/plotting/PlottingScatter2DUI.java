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

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.dawb.common.ui.plot.trace.ILineTrace;
import org.dawb.common.ui.plot.trace.ILineTrace.PointStyle;
import org.dawb.common.ui.plot.trace.ILineTrace.TraceType;
import org.eclipse.draw2d.ColorConstants;
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
	public void processPlotUpdate(final DataBean dbPlot, boolean isUpdate) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
				if (plotData != null) {

					AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS+0);
					AbstractDataset yAxisValues = dbPlot.getAxis(AxisMapBean.YAXIS+0);

					plottingSystem.clear();
					ILineTrace scatterPlotPoints = plottingSystem.createLineTrace(yAxisValues.getName());
					scatterPlotPoints.setTraceType(TraceType.POINT);
					scatterPlotPoints.setTraceColor(ColorConstants.blue);
					scatterPlotPoints.setPointStyle(PointStyle.FILLED_CIRCLE);
					scatterPlotPoints.setPointSize(6);
					scatterPlotPoints.setName(xAxisValues.getName());
					scatterPlotPoints.setData(xAxisValues, yAxisValues);
					plottingSystem.addTrace(scatterPlotPoints);
					plottingSystem.autoscaleAxes();
					logger.debug("Scatter plot created");
				}
			}
		});
	}
}
