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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.legend;

import java.util.LinkedList;

import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;

import gda.observable.IObservable;
import gda.observable.IObserver;

/**
 * Abstract LegendComponent that can be added to the DataSetPlotter as acting
 * legend
 */
public abstract class LegendComponent extends Composite implements IObservable {

	protected LinkedList<IObserver> observers;
	protected LinkedList<LegendChangeEventListener> listeners;
	
	/**
	 * @param parent
	 * @param style
	 */

	public LegendComponent(Composite parent, int style)
	{
		super(parent,style);
		observers = new LinkedList<IObserver>();
		listeners = new LinkedList<LegendChangeEventListener>();
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

	/**
	 * Add a LegendChangeEventListener
	 * @param listener LegendChangeEventListener that should be added
	 */
	
	public void addLegendChangeEventListener(LegendChangeEventListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Remove a LegendChangeEventListener
	 * @param listener LegendChangeEventListener that should be removed
	 */
	
	public void removeLegendChangeEventListener(LegendChangeEventListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Remove all LegendChangeEventListeners
	 */
	
	public void removeAllLegendChangeEventListener()
	{
		listeners.clear();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		if (listeners != null)
			listeners.clear();
		if (observers != null)
			observers.clear();
	}
	
	/**
	 * Update the legend table
	 * @param table List containing all the Plot appearance information
	 */
	public abstract void updateTable(Plot1DGraphTable table);

}
