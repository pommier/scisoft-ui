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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

import org.eclipse.swt.graphics.RGB;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.roi.GridROI;

/**
 * Class to aggregate information associated with a ROI
 * A GridROI is the same as a RectangularROI, but with grid information
 */
public class GridROIData extends RectangularROIData {

	/**
	 * @param roi
	 * @param data
	 */
	public GridROIData(GridROI roi, AbstractDataset data) {
		super(roi, data);
		plotColourRGB = new RGB(0,0,0);
	}

	/**
	 * @param roi
	 * @param profileData
	 * @param axes
	 * @param profileSum
	 */
	public GridROIData(GridROI roi, AbstractDataset[] profileData, AxisValues[] axes, double profileSum) {
		super(roi, profileData, axes, profileSum);
		plotColourRGB = new RGB(0,0,0);
	}
}
