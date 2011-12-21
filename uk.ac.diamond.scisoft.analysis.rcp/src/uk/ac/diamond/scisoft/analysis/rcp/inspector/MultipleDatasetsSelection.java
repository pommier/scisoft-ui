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
