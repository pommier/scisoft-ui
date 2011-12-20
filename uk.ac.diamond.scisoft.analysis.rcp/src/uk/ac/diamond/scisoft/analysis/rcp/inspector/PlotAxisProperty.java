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

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;

/**
 * Represent plot axis used in GUI and model
 */
public class PlotAxisProperty extends InspectorProperty {
	protected PlotAxis plotAxis;

	@Override
	public PlotAxisProperty clone() {
		PlotAxisProperty n = new PlotAxisProperty();
		n.plotAxis = plotAxis.clone();
		return n;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(plotAxis != null ? plotAxis.getName() : null);
		s.append(": ");
		s.append(pcl);
		return s.toString();
	}

	public PlotAxis getValue() {
		return plotAxis;
	}

	public void setValue(PlotAxis plotAxis) {
		PlotAxis oldValue = this.plotAxis;
		this.plotAxis = plotAxis;

		fire(new PropertyChangeEvent(this, "plotaxis", oldValue, plotAxis));
	}

	public boolean isInSet() {
		return plotAxis.isInSet();
	}

	public String getName() {
		return plotAxis != null ? plotAxis.getName() : null; 
	}

	public LinkedList<String> getNames() {
		return plotAxis != null ? plotAxis.getNames() : null; 
	}

	public void setName(String name) {
		setName(name, true);
	}

	public void clear() {
		if (plotAxis != null)
			plotAxis.clear();
	}

	public boolean containsName(String name) {
		if (plotAxis != null)
			return plotAxis.containsName(name);
		return false;
	}

	public void put(int dimension, String name) {
		if (plotAxis == null)
			plotAxis = new PlotAxis();

		plotAxis.putParameter(dimension, name);
	}

	public int getDimension() {
		return plotAxis != null ? plotAxis.getDimension() : -1;
	}

	public void setName(String name, boolean fire) {
		String oldName = null;
		if (plotAxis == null)
			plotAxis = new PlotAxis();
		else
			oldName = plotAxis.getName();

		plotAxis.setName(name);

		if (fire)
			fire(new PropertyChangeEvent(this, "plotaxis", oldName, name));
	}

	public void setInSet(boolean inSet) {
		if (plotAxis != null)
			plotAxis.setInSet(inSet);
	}
}
