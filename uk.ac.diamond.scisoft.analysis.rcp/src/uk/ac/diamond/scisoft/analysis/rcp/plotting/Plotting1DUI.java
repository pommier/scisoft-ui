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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.dawb.common.ui.plot.trace.ILineTrace;
import org.dawb.common.ui.plot.trace.ILineTrace.TraceType;
import org.dawb.common.ui.plot.trace.ITrace;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;

/**
 * Class to create a 1D plotting
 * 
 */
public class Plotting1DUI extends AbstractPlotUI {

	public final static String STATUSITEMID = "uk.ac.diamond.scisoft.analysis.rcp.plotting.Plotting1DUI";
	private static final Logger logger = LoggerFactory.getLogger(Plotting1DUI.class);

	private AbstractPlottingSystem plottingSystem;
	private List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());
	private String currentDataName;
	private String previousDataName;
	private String currentXAxisName;
	private String previousXAxisName;

	/**
	 * Constructor of a plotting 1D 
	 * @param plottingSystem plotting system
	 */
	public Plotting1DUI(AbstractPlottingSystem plottingSystem) {
		this.plottingSystem = plottingSystem;
	}

	@Override
	public void processPlotUpdate(final DataBean dbPlot, boolean isUpdate) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
				if (plotData != null) {

					final List<AbstractDataset> yDatasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());
					Collection<ITrace> previousTraces = plottingSystem.getTraces();
					final ArrayList<ITrace> traceList = new ArrayList<ITrace>(previousTraces);

					int i=0;
					int plotDataSize=plotData.size();
					String title = "";
					Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
					while (iter.hasNext()) {
						DataSetWithAxisInformation dataSetAxis = iter.next();
						AbstractDataset data = dataSetAxis.getData();
						yDatasets.add(data);
						currentDataName = data.getName();						
						if(i==0){
							title += data.getName();
						}													
						if(1==plotDataSize-1 && i==1){
							title += ","+data.getName();
						}					
						if(i==plotDataSize-1 && i!=0 && i!=1){						
							title += "..."+data.getName();
						}

						if(currentDataName.equals("")) // if no name given set default name
							currentDataName = "Y-Axis";
						final AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS);
						currentXAxisName = xAxisValues.getName();
						if(currentXAxisName.equals("")) // if no name given set default name
							currentXAxisName = "X-Axis";

						if(!traceList.isEmpty()&&traceList.size()>i){
							previousDataName = traceList.get(i).getName();
							if(traceList.get(i) instanceof ILineTrace){
								AbstractDataset xData = ((ILineTrace)traceList.get(i)).getXData();
								if(xData!=null)
									previousXAxisName = xData.getName();
							}
						} else {
							previousDataName = "";
							previousXAxisName = "";
						}

						// if same data being pushed to plot, we do an update instead of recreating the plot
						if(currentDataName.equals(previousDataName)&&currentXAxisName.equals(previousXAxisName)){
							ITrace plotTrace = plottingSystem.getTrace(currentDataName);
							if(plotTrace instanceof ILineTrace){
								ILineTrace lineTrace = (ILineTrace) plotTrace;
								lineTrace.setData(xAxisValues, data);
								lineTrace.repaint();
							}
//							TODO : perform an auto scale only if no zoom has been previously done
							plottingSystem.autoscaleAxes();
							// autoscale xAxis (in the stack plot use case)
							if(isAbsractPlottingXAxisAutoscaled())
								plottingSystem.getSelectedXAxis().setRange(xAxisValues.getDouble(xAxisValues.minPos()), xAxisValues.getDouble(xAxisValues.maxPos()));
							// autoscale yAxis (in the stack plot use case)
							if(isAbsractPlottingYAxisAutoscaled())
								plottingSystem.getSelectedYAxis().setRange(data.getDouble(data.minPos()), data.getDouble(data.maxPos()));
							plottingSystem.setTitle("Plot of "+title+" against "+currentXAxisName);
							logger.debug("Plot 1D updated");
						}// if x or y axis change then we create a new plot
						else if((!currentDataName.equals(previousDataName)||!currentXAxisName.equals(previousXAxisName))){
							plottingSystem.getSelectedYAxis().setTitle("");
							plottingSystem.clear();
							xAxisValues.setName(currentXAxisName);
							data.setName(currentDataName);
							Collection<ITrace> traces = plottingSystem.createPlot1D(xAxisValues, yDatasets, null);
							//plottingSystem.setShowLegend(false);
							for (ITrace iTrace : traces) {
								final ILineTrace lineTrace = (ILineTrace)iTrace;
								lineTrace.setTraceType(TraceType.SOLID_LINE);
							}
							logger.debug("Plot 1D created");
						}
						i++;
					}

					// remove the traces that are not part of the yDataset anymore 
					// (in the case of the stack plot item number decrease for instance)
					if(yDatasets.size()<traceList.size()){
						for(int j=yDatasets.size(); j<traceList.size(); j++){
							plottingSystem.removeTrace(traceList.get(j));
						}
					}
				}
			}
		});
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
		observers.removeAll(observers);
	}

	private boolean isAbsractPlottingXAxisAutoscaled() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_X_AXIS_AUTOSCALE) ? 
				preferenceStore.getDefaultBoolean(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_X_AXIS_AUTOSCALE)
				: preferenceStore.getBoolean(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_X_AXIS_AUTOSCALE);
	}

	private boolean isAbsractPlottingYAxisAutoscaled() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_Y_AXIS_AUTOSCALE) ? 
				preferenceStore.getDefaultBoolean(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_Y_AXIS_AUTOSCALE)
				: preferenceStore.getBoolean(PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_Y_AXIS_AUTOSCALE);
	}
}
