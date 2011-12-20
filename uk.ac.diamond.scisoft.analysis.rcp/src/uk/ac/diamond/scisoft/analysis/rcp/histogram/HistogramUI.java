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

package uk.ac.diamond.scisoft.analysis.rcp.histogram;

import gda.observable.IObserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IActionBars;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AbstractPlotUI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEvent;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;

/**
 *
 */
public class HistogramUI extends AbstractPlotUI {

	private List<IObserver> observers = 
		Collections.synchronizedList(new LinkedList<IObserver>());
	
	private static HashMap<String, Boolean> autoScaleSettings = new HashMap<String,Boolean>();
	
	private Action zoomAction;
	private Action activateZoom;
	private Action lockScale;
	private Action autoScale;
	private Action showGraphLines;
	private HistogramView histoView;
	/**
	 * @param view
	 * @param bars
	 * @param plotter
	 * 
	 */
	
	public HistogramUI(HistogramView view,
					   IActionBars bars, 
			           final DataSetPlotter plotter)
	{
		this.histoView = view;
		buildToolActions(bars.getToolBarManager(), plotter);
	}	
	

	private void buildToolActions(IToolBarManager manager, final DataSetPlotter plotter)
	{
		zoomAction = new Action()
		{
			@Override
			public void run()
			{
				plotter.undoZoom();
			}
		};
		zoomAction.setText("Undo zoom");
		zoomAction.setToolTipText("Undo a zoom level");
		zoomAction.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/minify.png"));
		activateZoom = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				plotter.setZoomEnabled(activateZoom.isChecked());
			}
		};

		activateZoom.setText("Zoom");
		activateZoom.setToolTipText("Zoom mode");
		activateZoom.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/magnify.png"));
		autoScale = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				if (!lockScale.isChecked()) {
					histoView.setAutoHistogramScaling(autoScale.isChecked());
					autoScaleSettings.put(histoView.getPartName(),autoScale.isChecked());
					histoView.createInitialHistogram();
				} else 
					autoScale.setChecked(false);
			}
		};
		autoScale.setText("Autohistogram");
		autoScale.setToolTipText("Auto stretch histogram");
		autoScale.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/computer_edit.png"));
		autoScale.setChecked(getPreferenceAutohistoChoice());
		histoView.setAutoHistogramScaling(autoScale.isChecked());
		
		lockScale = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				histoView.setHistogramLock(lockScale.isChecked());
				if (lockScale.isChecked()) {
					histoView.setAutoHistogramScaling(false);
					autoScale.setChecked(false);
				}
			}
		};
		lockScale.setText("Lock histogram");
		lockScale.setToolTipText("Lock the histogram region for subsequent images");
		lockScale.setChecked(false);
		lockScale.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/lock.png"));
		
		showGraphLines = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				histoView.setGraphLines(showGraphLines.isChecked());
			}
		};
		showGraphLines.setText("Show channel graphs");
		showGraphLines.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/chart_curve.png"));
		showGraphLines.setToolTipText("Active/Deactivate channel graphs");
		showGraphLines.setChecked(true);
		
		manager.add(activateZoom);
		manager.add(zoomAction);
		manager.add(autoScale);
		manager.add(lockScale);
		manager.add(showGraphLines);
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
	public void plotActionPerformed(PlotActionEvent event) {
		// Nothing to do

	}
	
	private void notifyObservers(Object event)
	{
		Iterator<IObserver> iter = observers.iterator();
		while (iter.hasNext())
		{
			IObserver ob = iter.next();
			ob.update(this, event);
		}		
	}
	

	@Override
	public void areaSelected(AreaSelectEvent event) {
		// pass on the AreaSelectEvent via the Object observed 
		// mechanism, yes repacking the event object is a bit evil
		
		notifyObservers(event);        
	}


	private boolean getPreferenceAutohistoChoice() {
		if (histoView != null &&
			autoScaleSettings.get(histoView.getPartName()) != null) {
			return autoScaleSettings.get(histoView.getPartName());
		}
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO) ? 
				preferenceStore.getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO)
				: preferenceStore.getBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO);
	}
	
}
