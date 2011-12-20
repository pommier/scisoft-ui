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

package uk.ac.diamond.scisoft.analysis.rcp.histogram;

import org.eclipse.jface.viewers.ISelection;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;

/**
 *
 */
public class HistogramDataUpdate implements ISelection {

	private AbstractDataset dataset;
	
	/**
	 * Constructor of a HistogramDataUpdate
	 * @param dataset Dataset associated to this HistogramDataUpdate
	 */
	
	public HistogramDataUpdate(IDataset dataset)
	{
		this.dataset = DatasetUtils.convertToAbstractDataset(dataset);
	}
	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Get the dataset associated to this update
	 * @return associated dataset
	 */
	public AbstractDataset getDataset()
	{
		return dataset;
	}
	

}
