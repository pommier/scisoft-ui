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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection.InspectorType;

public class MultipleDatasetsSelection implements IStructuredSelection {

	private List<ILazyDataset> datasets;
	final private InspectorType view;

	/**
	 * Enclosing class for list of axis selection
	 */
	public class AxesSelection extends ArrayList<AxisSelection> implements List<AxisSelection> {
		
	}

	final private List<AxesSelection> axesList;

	public MultipleDatasetsSelection() {
		this(InspectorType.EMPTY);
	}

	public MultipleDatasetsSelection(InspectorType type) {
		axesList = new ArrayList<MultipleDatasetsSelection.AxesSelection>();
		datasets = new ArrayList<ILazyDataset>();
		view = type;
	}

	@Override
	public boolean isEmpty() {
		return datasets.size() == 0;
	}

	@Override
	public ILazyDataset getFirstElement() {
		return isEmpty() ? null : datasets.get(0);
	}

	@Override
	public Iterator<ILazyDataset> iterator() {
		return toList().iterator();
	}

	@Override
	public int size() {
		return datasets.size();
	}

	@Override
	public ILazyDataset[] toArray() {
		return datasets.toArray(new ILazyDataset[0]);
	}

	@Override
	public List<ILazyDataset> toList() {
		return datasets;
	}

	/**
	 * Get a list of axis selection
	 * @param index
	 * @return axes selection 
	 */
	public List<AxisSelection> getAxes(int index) {
		return axesList.get(index);
	}

	public InspectorType getType() {
		return view;
	}

	public void addDatasetSelection(ILazyDataset dataset, List<AxisSelection> axes) {
		datasets.add(dataset);
		AxesSelection sel = new AxesSelection();
		sel.addAll(axes);
		axesList.add(sel);
	}
}
