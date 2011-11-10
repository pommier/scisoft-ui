/*-
 * Copyright © 2009 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
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
