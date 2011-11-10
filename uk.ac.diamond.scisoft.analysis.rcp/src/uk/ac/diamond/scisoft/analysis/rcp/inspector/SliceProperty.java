/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.beans.PropertyChangeEvent;

import uk.ac.diamond.scisoft.analysis.dataset.Slice;

/**
 * Represent slice used in GUI and model
 */
public class SliceProperty extends InspectorProperty {
	protected Slice slice;
	protected int max = -1; // maximum size

	@Override
	public SliceProperty clone() {
		SliceProperty n = new SliceProperty();
		n.slice = slice.clone();
		n.max = max;
		return n;
	}

	@Override
	public String toString() {
		return slice != null ? slice.toString() : ":";
	}

	public Slice getValue() {
		return slice;
	}

	public void setValue(Slice slice) {
		Slice oldValue = this.slice;
		this.slice = slice;

		fire(new PropertyChangeEvent(this, "slice", oldValue, slice));
	}

	public void setStart(int start) {
		Integer oldStart = null;
		if (slice == null)
			slice = new Slice(start, null);
		else {
			oldStart = slice.getStart();
			slice.setStart(start);
		}

		fire(new PropertyChangeEvent(this, "slice", oldStart, start));
	}

	public void setStop(int stop) {
		Integer oldStop = null;
		if (slice == null)
			slice = new Slice(stop);
		else {
			oldStop = slice.getStop();
			slice.setStop(stop);
		}

		fire(new PropertyChangeEvent(this, "slice", oldStop, stop));
	}

	public void setLength(int length) {
		max = length;
		if (slice == null)
			slice = new Slice();
		slice.setLength(length);
	}

	public void setStep(int step) {
		int oldStep = 1;
		if (slice == null)
			slice = new Slice(null, null, step);
		else {
			oldStep = slice.getStep();
			slice.setStep(step);
		}

		fire(new PropertyChangeEvent(this, "slice", oldStep, step));
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMax() {
		return max;
	}
}
