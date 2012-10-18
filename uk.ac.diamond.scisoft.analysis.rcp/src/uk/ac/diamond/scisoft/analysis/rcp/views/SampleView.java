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

package uk.ac.diamond.scisoft.analysis.rcp.views;


import gda.observable.IObserver;

import java.util.LinkedList;

import org.dawnsci.plotting.jreality.core.AxisMode;
import org.dawnsci.plotting.jreality.impl.PlotException;
import org.dawnsci.plotting.jreality.overlay.DemoOverlay;
import org.dawnsci.plotting.jreality.tool.AreaSelectEvent;
import org.dawnsci.plotting.jreality.tool.PlotActionEvent;
import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingMode;
import uk.ac.diamond.scisoft.analysis.rcp.views.plot.AbstractPlotView;
import uk.ac.diamond.scisoft.analysis.rcp.views.plot.PlotBean;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends AbstractPlotView implements IPlotUI {

	@Override
	protected IPlotUI createPlotActions(final Composite parent) {
		return this;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		LinkedList<IDataset> dataSets = new LinkedList<IDataset>();
		DoubleDataset d1 = DoubleDataset.arange(256*256);
		d1 = (DoubleDataset) d1.reshape(256,256);
		plotter.setAxisModes(AxisMode.LINEAR, AxisMode.LINEAR, AxisMode.LINEAR);
		plotter.setMode(PlottingMode.TWOD);
		dataSets.add(d1);
		try {
			plotter.replaceAllPlots(dataSets);
		} catch (PlotException e) {
			e.printStackTrace();
		}
		plotter.registerOverlay(new DemoOverlay());
		
	}
	@Override
	protected String getGraphTitle() {
		return "Bingo";
	}

	@Override
	protected String getXAxis() {
		return "X-Axis";
	}

	@Override
	protected String getYAxis() {
		return "Y-Axis";
	}

	@Override
	public PlotBean getPlotBean() {
		return null;
	}
	@Override
	public void deactivate(boolean leaveSidePlotOpen) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void disposeOverlays() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processGUIUpdate(GuiBean guiBean) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addIObserver(IObserver observer) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteIObserver(IObserver observer) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteIObservers() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void plotActionPerformed(PlotActionEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void areaSelected(AreaSelectEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SidePlotView getSidePlotView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SidePlotView initSidePlotView() {
		return null;
	}
}
