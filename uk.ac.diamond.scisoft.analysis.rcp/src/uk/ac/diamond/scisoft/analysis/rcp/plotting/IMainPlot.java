/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;


import java.util.List;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;


public interface IMainPlot {

	/**
	 * Returns the current DataSet from the main plot
	 * @return current dataset
	 */
	public abstract IDataset getCurrentDataSet();

	/**
	 * 
	 * @return a list of Datasets, may be null
	 */
			
	public abstract List<IDataset> getCurrentDataSets();

	/**
	 * 
	 * @return list of x data values, may be null
	 */
	public abstract List<AxisValues> getXAxisValues();

	/**
	 * Returns true of the plotted is disposed and no
	 * longer available for plotting.
	 * @return true if disposed.
	 */
	public abstract boolean isDisposed();

}
