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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.PlotActionEventListener;

/**
 * Class which can be extended to create custom toolbars.
 */
public class Plotting1DUIAdapter extends AbstractPlotUI {

	/**
	 * String placeholder for the History plots
	 */
	public final static String HISTORYSTRING = "History";

	protected List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());
	protected AbstractPlottingSystem plotter;
	protected Composite parent;

	protected static final Logger logger = LoggerFactory.getLogger(Plotting1DUIAdapter.class);

	/**
	 * Constructor of a Plot1DUI
	 * 
	 * @param plotter
	 *            Plotter object for parsing information back to the plotter
	 * @param parent
	 *            parent composite
	 */
	public Plotting1DUIAdapter(final AbstractPlottingSystem plotter, final Composite parent) {
		this.parent = parent;
		this.plotter = plotter;
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

	private Set<PlotActionEventListener> plotListeners;

	/**
	 * Call to listen to PlotActionEvents outside the PlotUI mechanism
	 * 
	 * @param l
	 */
	public void addPlotActionEventListener(final PlotActionEventListener l) {
		if (plotListeners == null)
			plotListeners = new LinkedHashSet<PlotActionEventListener>(3);
		plotListeners.add(l);
	}

	@Override
	public void plotActionPerformed(PlotActionEvent event) {
		if (plotListeners != null) {
			for (PlotActionEventListener l : plotListeners)
				l.plotActionPerformed(event);
		}
	}

	/**
	 * Please overrider, default nothing. @see Plot1DUIComplete
	 * @param manager
	 */
	public void buildStatusLineItems(@SuppressWarnings("unused") IStatusLineManager manager) {
		
	}

	@Override
	public void dispose() {
		if (plotListeners != null)
			plotListeners.clear();
		plotListeners = null;
	}

}
