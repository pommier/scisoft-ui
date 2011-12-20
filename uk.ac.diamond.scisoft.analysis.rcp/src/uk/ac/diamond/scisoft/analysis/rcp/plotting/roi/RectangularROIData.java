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

import uk.ac.diamond.scisoft.analysis.dataset.AbstractCompoundDataset;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.roi.ROIProfile;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;

/**
 * Class to aggregate information associated with a ROI
 */
public class RectangularROIData extends ROIData {
	/**
	 * Construct new object from given roi and data
	 * @param rroi
	 * @param data
	 */
	public RectangularROIData(RectangularROI rroi, AbstractDataset data) {
		this(rroi, data, null, 1.);
	}

	public RectangularROIData(RectangularROI rroi, AbstractDataset data, AbstractDataset mask) {
		this(rroi, data, mask, 1.);
	}
	
	/**
	 * Construct new object from given roi and data
	 * 
	 * @param rroi
	 * @param data
	 * @param subFactor
	 */
	public RectangularROIData(RectangularROI rroi, AbstractDataset data, AbstractDataset mask, double subFactor) {
		super();

		setROI(rroi.copy());
		roi.downsample(subFactor);
		profileData = ROIProfile.box(data, mask, (RectangularROI) roi);
		if (profileData != null && profileData[0].getShape()[0] > 1 && profileData[1].getShape()[0] > 1) {
			AbstractDataset pdata;
			for (int i = 0; i < 2; i++) {
				pdata = profileData[i];
				if (pdata instanceof AbstractCompoundDataset) // use first element
					profileData[i] = ((AbstractCompoundDataset) pdata).getElements(0);
			}
			Number sum = (Number) profileData[0].sum();
			profileSum = sum.doubleValue();

			xAxes = new AxisValues[] { null, null };
			xAxes[0] = new AxisValues();
			xAxes[1] = new AxisValues();

			AbstractDataset axis;
			axis = profileData[0].getIndices().squeeze();
			axis.imultiply(subFactor);
			xAxes[0].setValues(axis);
			axis = profileData[1].getIndices().squeeze();
			axis.imultiply(subFactor);
			xAxes[1].setValues(axis);
		} else {
			setPlot(false);
		}
	}

	/**
	 * Aggregate a copy of ROI data to this object
	 * @param roi
	 * @param profileData
	 * @param axes
	 * @param profileSum
	 */
	public RectangularROIData(RectangularROI roi, AbstractDataset[] profileData, AxisValues[] axes, double profileSum) {
		super();
		setROI(roi.copy());
		this.profileData = profileData.clone();
		for (int i = 0; i < profileData.length; i++) {
			this.profileData[i] = profileData[i].clone();
		}
		xAxes = axes.clone();
		for (int i = 0; i < axes.length; i++) {
			xAxes[i] = axes[i].clone();
		}
		this.profileSum = profileSum;
		setPlot(false);
	}

	/**
	 * @return linear region of interest
	 */
	@Override
	public RectangularROI getROI() {
		return (RectangularROI) roi;
	}
}
