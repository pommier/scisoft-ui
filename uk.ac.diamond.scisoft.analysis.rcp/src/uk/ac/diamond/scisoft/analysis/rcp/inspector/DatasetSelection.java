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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;

/**
 * Class to encapsulate selection of items for dataset inspector
 */
public class DatasetSelection implements IStructuredSelection {

	public enum InspectorType {
		LINE, POINTS1D, LINESTACK, DATA1D, POINTS2D, IMAGE, MULTIIMAGE, SURFACE, DATA2D, POINTS3D, VOLUME, EMPTY;
		private static int index = 0;
		private int value;
		private static int getIndex() {
			return index++;
		}

		private InspectorType() {
			value = getIndex();
		}

		public int getValue() {
			return value;
		}

		static public InspectorType getType(int type) {
			for (InspectorType t : InspectorType.values())
				if (t.getValue() == type)
					return t;
			return EMPTY;
		}
	}

	final private List<AxisSelection> axes;
	final private ILazyDataset[] datasets;
	final private InspectorType view;

	/**
	 * Null constructor to not show any plot or view
	 */
	public DatasetSelection() {
		axes = new ArrayList<AxisSelection>();
		datasets = new ILazyDataset[0];
		view = InspectorType.EMPTY;
	}

	/**
	 * Show a line plot
	 * @param axes (can be null)
	 * @param dataset
	 */
	public DatasetSelection(List<AxisSelection> axes, ILazyDataset... dataset) {
		this.axes = axes;
		this.datasets = dataset;
		view = InspectorType.LINE;
	}

	/**
	 * @param type inspector used
	 * @param axes
	 * @param dataset
	 */
	public DatasetSelection(InspectorType type, List<AxisSelection> axes, ILazyDataset... dataset) {
		this.axes = axes;
		this.datasets = dataset;
		this.view = type;
	}

	@Override
	public String toString() {
		if (isEmpty())
			return "Null selection";
		return Arrays.toString(datasets);
	}

	@Override
	public boolean isEmpty() {
		return datasets == null || datasets.length == 0;
	}

	@Override
	public ILazyDataset getFirstElement() {
		return isEmpty() ? null : datasets[0];
	}

	@Override
	public Iterator<ILazyDataset> iterator() {
		return toList().iterator();
	}

	@Override
	public int size() {
		return isEmpty() ? 0 : datasets.length;
	}

	@Override
	public ILazyDataset[] toArray() {
		return datasets;
	}

	@Override
	public List<ILazyDataset> toList() {
		List<ILazyDataset> c = new ArrayList<ILazyDataset>();
		if (!isEmpty())
			Collections.addAll(c, datasets);
		return c;
	}

	/**
	 * @return true if (lazy) datasets are same shape and axes are the same (in value)  
	 */
	@Override
	public boolean equals(Object other) {
		return almostEquals(other);
	}

	/**
	 * @return true if (lazy) datasets are same shape and axes are the same (in value)  
	 */
	final public boolean almostEquals(Object other) {
		if (other instanceof DatasetSelection) {
			DatasetSelection that = (DatasetSelection) other;
			if (!areAxesAllEqual(axes, that.axes))
				return false;
			if (areDatasetsAllEqual(datasets, that.datasets))
				return true;
		}
		return false;
	}

	private boolean areDatasetsAllEqual(ILazyDataset[] aList, ILazyDataset[] bList) {
		if (aList == bList)
			return true;
		if (aList == null || bList == null)
			return false;
		int size = aList.length;
		if (bList.length != size)
			return false;
		for (int i = 0; i < size; i++) {
			ILazyDataset a = aList[i];
			ILazyDataset b = bList[i];
			if (a == b)
				continue;
			if (!Arrays.equals(a.getShape(), b.getShape()))
				return false;
			if (!a.getName().equals(b.getName())) {
				return false;
			}
		}
		return true;
	}

	private boolean areAxesAllEqual(List<AxisSelection> aList, List<AxisSelection> bList) {
		if (aList == bList)
			return true;
		if (aList == null || bList == null)
			return false;
		int size = aList.size();
		if (bList.size() != size)
			return false;
		for (int i = 0; i < size; i++) {
			AxisSelection a = aList.get(i);
			AxisSelection b = bList.get(i);
			if (!a.equals(b))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = view.getValue();
		for (int i = 0, imax = datasets.length; i < imax; i++) {
			hash = hash * 31 + datasets[i].hashCode();
		}
		for (int i = 0, imax = axes == null ? 0 : axes.size(); i < imax; i++) {
			hash = hash * 31 + axes.get(i).hashCode();
		}
		return hash;
	}

	/**
	 * @return list of axes (can be null)
	 */
	public List<AxisSelection> getAxes() {
		return axes;
	}

	public InspectorType getType() {
		return view;
	}

}
