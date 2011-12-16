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

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.ListOrderedMap;


/**
 * Class to hold a list of axis names and dataset from which an axis can be selected
 */
public class AxisSelection extends InspectorProperty {
	
	/**
	 * Subclass ListOrderedMap to avoid casting of the generic types in the rest of the code
	 *
	 */
	class AxisSelDataOrderedMap extends ListOrderedMap {
		public AxisSelDataOrderedMap() {
			super();
		}
		
		public AxisSelData get(String key) {
			return (AxisSelData) super.get(key);
		}
		
		@Override
		public String get(int index) {
			return (String) super.get(index);
		}
		
		@Override
		public AxisSelData getValue(int index) {
			return (AxisSelData) super.getValue(index);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Set<String> keySet() {
			return super.keySet();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<AxisSelData> valueList() {
			return super.valueList();
		}
	}
	
	class AxisSelData implements Comparable<AxisSelData> {
		private boolean selected;
		private int order; // possible order in a list of choices (0 signifies leave to end of list) 
		private AxisChoice data;

		public AxisSelData(AxisChoice axisData, boolean axisSelected) {
			setData(axisData);
			setSelected(axisSelected);
		}

		/**
		 * @param selected The selected to set.
		 */
		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		/**
		 * @return Returns the selected.
		 */
		public boolean isSelected() {
			return selected;
		}

		/**
		 * @param order The order to set.
		 */
		public void setOrder(int order) {
			this.order = order;
		}

		/**
		 * @return Returns the order.
		 */
		public int getOrder() {
			return order;
		}

		/**
		 * @param data The data to set.
		 */
		public void setData(AxisChoice data) {
			this.data = data;
		}

		/**
		 * @return Returns the data.
		 */
		public AxisChoice getData() {
			return data;
		}

		@Override
		public int compareTo(AxisSelData axisSelData) {
			int cOrder = axisSelData.getOrder();
			if (order == 0)
				return cOrder == 0 ? 0 : 1;
			if (cOrder == 0)
				return -1;
			return order - cOrder;
		}
		
	}

	private int length; // length of axis
	private AxisSelDataOrderedMap asData;

	Transformer axisDataTransformer = new Transformer() {  
		@Override
		public Object transform(Object o) {
			if (o instanceof AxisSelData)
				return ((AxisSelData) o).getData();
			return null;
		}  
	};
	
	Transformer orderTransformer = new Transformer() {  
		@Override
		public Object transform(Object o) {
			if (o instanceof AxisSelData)
				return ((AxisSelData) o).getOrder();
			return null;
		}  
	};
	
	Predicate axisSelectionPredicate = new Predicate() {  
		@Override
		public boolean evaluate(Object o) {  
			if (o instanceof AxisSelData)
				return ((AxisSelData) o).isSelected();
			return false;  
		}  
	}; 
	
	/**
	 * Create an axis selection that corresponds to a dataset dimension of given length
	 * @param length 
	 */
	public AxisSelection(int length) {
		this.length = length;
		asData = new AxisSelDataOrderedMap();
	}

	/**
	 * @return Returns the length
	 */
	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append('(');
		for (String n : asData.keySet()) {
			text.append(n);
			if (asData.get(n).isSelected()) {
				text.append('*');
			}
			text.append(", ");
		}
		if (text.length() > 0) {
			text.deleteCharAt(text.length()-1);
			text.deleteCharAt(text.length()-1);
		}
		text.append(')');
		return text.toString();
	}

	/**
	 * Add choice
	 * @param axis
	 */
	public void addSelection(AxisChoice axis, int order) {
		String name = axis.getName();
		AxisSelData data;
		if (asData.containsKey(name)) {
			// already added name
			data = asData.get(name); 
			if (axis != data.getData())
				data.setData(axis);
		} else {
			asData.put(name, new AxisSelData(axis, false));
			data = asData.get(name);
		}
		data.setOrder(order);
	}

	/**
	 * @param name
	 * @return true if name is one of possible selections
	 */
	public boolean containsAxis(String name) {
		return asData.containsKey(name);
	}

	/**
	 * Select an axis with given name
	 * @param name
	 */
	public void selectAxis(String name) {
		selectAxis(name, true);
	}

	/**
	 * Select an axis with given name
	 * @param name
	 * @param fire
	 */
	public void selectAxis(String name, boolean fire) {
		String oldName = getSelectedName();
		for (String a: asData.keySet())
			asData.get(a).setSelected(false);
		asData.get(name).setSelected(true);

		if (fire)
			fire(new PropertyChangeEvent(this, "axisselection", oldName, name));
	}

	/**
	 * Select an axis with given index
	 * @param index 
	 */
	public void selectAxis(int index) {
		selectAxis(asData.get(index));
	}

	/**
	 * @param index 
	 * @return axis name of given index
	 */
	public String getName(int index) {
		return asData.get(index);
	}

	/**
	 * @param name
	 * @return dimension data for the given name
	 */
	public int[] getDimensions(String name) {
		AxisSelData data = asData.get(name);
		return data == null ? null : data.getData().getAxes();
	}

	/**
	 * @param index 
	 * @return axis data of given index
	 */
	public AxisChoice getAxis(int index) {
		AxisSelData data = asData.getValue(index); 
		return data == null ? null : data.getData();
	}

	/**
	 * @param name
	 * @return axis data of given name
	 */
	public AxisChoice getAxis(String name) {
		AxisSelData s = asData.get(name);
		return s == null ? null : s.getData();
	}

	/**
	 * @return number of names
	 */
	public int size() {
		return asData.size();
	}

	/**
	 * @param index
	 * @return selection status
	 */
	public boolean isSelected(int index) {
		AxisSelData data = asData.getValue(index); 
		return (data == null) ? false : data.isSelected();
	}

	/**
	 * Get index of selected axis
	 * @return index or -1 if nothing selected
	 */
	public int getSelectedIndex() {
		AxisChoice choice = getSelectedAxis();
		return asData.indexOf(choice);
	}

	/**
	 * Get name of selected axis
	 * @return name or null if nothing selected
	 */
	public String getSelectedName() {
		AxisChoice choice = getSelectedAxis();
		if (choice != null)
			return choice.getName();	
		return null;
	}

	/**
	 * @return selected dimensions
	 */
	public int[] getSelectedDimensions() {
		AxisChoice choice = getSelectedAxis();
		if (choice != null)
			return choice.getAxes();	
		return null;
	}

	/**
	 * @return selected dataset
	 */
	public AxisChoice getSelectedAxis() {
		AxisSelData sel = (AxisSelData) CollectionUtils.find(asData.valueList(), axisSelectionPredicate);
		return (sel == null) ? null :sel.getData();
	}

	/**
	 * Call this once finished adding selections to reorder names
	 */
	public void reorderNames() {
	    List<AxisSelData> asDataArray = asData.valueList();
	    
		Collections.sort(asDataArray);
		
	    AxisSelDataOrderedMap newAsData = new AxisSelDataOrderedMap();
		for (AxisSelData data : asDataArray) {
			String key = data.getData().getName();
			newAsData.put(key, data);
		}
	    asData = newAsData;
	}

	/**
	 * @return maximum order
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int getMaxOrder() {
		Collection orders = CollectionUtils.collect(asData.values(), orderTransformer);
		return orders.size() > 0 ? (Integer) Collections.max(orders) : 0;
	}

	/**
	 * @return true if names and axis datasets have same values
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (other instanceof AxisSelection) {
			AxisSelection that = (AxisSelection) other;
			
			if (!CollectionUtils.isEqualCollection(that.asData.keySet(), asData.keySet()))
				return false;
			
			Collection asChoice = CollectionUtils.collect(asData.values(), axisDataTransformer);  
			Collection thatChoice = CollectionUtils.collect(that.asData.values(), axisDataTransformer);  
			
			if (!CollectionUtils.isEqualCollection(asChoice, thatChoice))
				return false;
			
			return true;
			
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = length;
		for (Object n : asData.keyList())
				hash = hash * 17 + n.hashCode();
		for (Object d : asData.valueList())
				hash = hash * 17 + d.hashCode();
		return hash;
	}

	/**
	 * Clone everything but axis choice values
	 */
	@Override
	public AxisSelection clone() throws CloneNotSupportedException {
		AxisSelection selection = new AxisSelection(length);
		for (int i = 0, imax = asData.size(); i < imax; i++) {
			AxisSelData data = asData.getValue(i);
			selection.addSelection(data.getData().clone(), data.getOrder());
		}
		return selection;
	}
}
