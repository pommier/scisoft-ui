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

public class HDF5AxisBean {
	private ILazyDataset cData; // chosen dataset
	private List<AxisSelection> axes; // list of axes for each dimension
	public ILazyDataset getcData() {
		return cData;
	}
	public void setcData(ILazyDataset cData) {
		this.cData = cData;
	}
	public List<AxisSelection> getAxes() {
		return axes;
	}
	public void setAxes(List<AxisSelection> axes) {
		this.axes = axes;
	}

}
