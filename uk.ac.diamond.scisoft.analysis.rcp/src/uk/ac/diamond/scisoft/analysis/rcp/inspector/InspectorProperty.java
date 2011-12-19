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
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

public abstract class InspectorProperty {
	protected Set<PropertyChangeListener> pcl = new HashSet<PropertyChangeListener>();

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if( listener != null)
			pcl.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if( listener != null)
			pcl.remove(listener);
	}

	protected void fire(PropertyChangeEvent event) {
		for (PropertyChangeListener l : pcl) {
			if( l != null)
				l.propertyChange(event);
		}
	}
}
