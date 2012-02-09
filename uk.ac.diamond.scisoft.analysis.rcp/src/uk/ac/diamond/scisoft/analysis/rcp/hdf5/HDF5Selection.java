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

package uk.ac.diamond.scisoft.analysis.rcp.hdf5;

import java.util.List;

import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection;

public class HDF5Selection extends DatasetSelection {
	
	private String fileName;
	private String node;

	public HDF5Selection(InspectorType type, String filename, String node, List<AxisSelection> axes, ILazyDataset... dataset) {
		super(type, axes, dataset);
		fileName = filename;
		this.node = node;
	}

	@Override
	public boolean equals(Object other) {
		if (super.equals(other) && other instanceof HDF5Selection) {
			HDF5Selection that = (HDF5Selection) other;
			if (fileName == null && that.fileName == null)
				return node.equals(that.node);
			if (fileName != null && fileName.equals(that.fileName))
				return node.equals(that.node);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 17 + node.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return node + " = " + super.toString();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String filename) {
		fileName = filename;
	}

	public String getNode() {
		return node;
	}
}
