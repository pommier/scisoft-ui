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
		// allow concurrent modifications by make copy
		HashSet<PropertyChangeListener> ls = new HashSet<PropertyChangeListener>(pcl);
		for (PropertyChangeListener l : ls) {
			if (l != null)
				l.propertyChange(event);
		}
	}
}
